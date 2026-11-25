package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.CotizacionRecienteDTO;
import com.cotizacion.cotizacion_backend.dto.ReportesDTO;
import com.cotizacion.cotizacion_backend.entity.Cotizacion;
import com.cotizacion.cotizacion_backend.entity.CotizacionDetalle;
import com.cotizacion.cotizacion_backend.repository.CotizacionDetalleRepository;
import com.cotizacion.cotizacion_backend.repository.CotizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteService {

    @Autowired private CotizacionRepository cotizacionRepo;
    @Autowired private CotizacionDetalleRepository detalleRepo;

    public ReportesDTO obtenerReporte(String estado, String familia, String periodo) {
        String estadoFiltro = estado == null ? "" : estado.trim();
        String familiaFiltro = familia == null ? "" : familia.trim();
        LocalDateTime fechaInicio = calcularFechaInicio(periodo);

        Long total = cotizacionRepo.countConFiltro(estadoFiltro, familiaFiltro, fechaInicio);


        boolean sinFiltroEstado = estadoFiltro.isEmpty();
        Long aprobadas = sinFiltroEstado || "APROBADO".equalsIgnoreCase(estadoFiltro)
                ? cotizacionRepo.countConFiltro("APROBADO", familiaFiltro, fechaInicio)
                : 0L;
        Long rechazadas = sinFiltroEstado || "RECHAZADA".equalsIgnoreCase(estadoFiltro)
                ? cotizacionRepo.countConFiltro("RECHAZADA", familiaFiltro, fechaInicio)
                : 0L;

        Double tasa = total > 0 ? (aprobadas.doubleValue() / total) * 100 : 0.0;

        // --- 1. Gráfico de Barras ---
        List<Object[]> rawMes = cotizacionRepo.getReporteMensual(estadoFiltro, familiaFiltro, fechaInicio);
        List<ReportesDTO.MesReporteDTO> porMes = new ArrayList<>();
        for (Object[] obj : rawMes) {
            porMes.add(ReportesDTO.MesReporteDTO.builder()
                    .mes((String) obj[0])
                    .ganadas(((Number) obj[1]).longValue())
                    .rechazadas(((Number) obj[2]).longValue())
                    .build());
        }

        // --- 2. Gráfico Dona (Motivos) ---
        List<Object[]> rawMotivos = cotizacionRepo.getMotivosRechazo(estadoFiltro, familiaFiltro, fechaInicio);
        List<ReportesDTO.MotivoRechazoDTO> motivos = new ArrayList<>();
        for (Object[] obj : rawMotivos) {
            Long cant = ((Number) obj[1]).longValue();
            motivos.add(ReportesDTO.MotivoRechazoDTO.builder()
                    .motivo((String) obj[0])
                    .cantidad(cant)
                    .porcentaje(rechazadas > 0 ? (cant.doubleValue() / rechazadas) * 100 : 0.0)
                    .build());
        }

        // --- 3. Tabla Productos (Calculando porcentaje real) ---
        List<Object[]> rawProductos = detalleRepo.getProductosMasVendidos(estadoFiltro, familiaFiltro, fechaInicio);
        List<ReportesDTO.RankingDTO> productos = new ArrayList<>();

        long totalProductos = rawProductos.stream().mapToLong(obj -> ((Number) obj[1]).longValue()).sum();

        for (Object[] obj : rawProductos) {
            long cant = ((Number) obj[1]).longValue();
            productos.add(ReportesDTO.RankingDTO.builder()
                    .nombre((String) obj[0])
                    .cantidad(cant)
                    .porcentaje(totalProductos > 0 ? (cant * 100.0) / totalProductos : 0.0)
                    .build());
        }

        // --- 4. Tabla Características ---
        List<Object[]> rawCaract = detalleRepo.getCaracteristicasMasSolicitadas(estadoFiltro, familiaFiltro, fechaInicio);
        List<ReportesDTO.RankingDTO> caracteristicas = new ArrayList<>();

        long totalCaract = rawCaract.stream().mapToLong(obj -> ((Number) obj[1]).longValue()).sum();

        for (Object[] obj : rawCaract) {
            long cant = ((Number) obj[1]).longValue();
            caracteristicas.add(ReportesDTO.RankingDTO.builder()
                    .nombre((String) obj[0])
                    .cantidad(cant)
                    .porcentaje(totalCaract > 0 ? (cant * 100.0) / totalCaract : 0.0)
                    .build());
        }

        // --- 5. Últimas 5 cotizaciones ---
        List<Cotizacion> recientesRaw = cotizacionRepo.findRecientesConFiltros(
                estadoFiltro, familiaFiltro, fechaInicio, PageRequest.of(0, 5));
        List<CotizacionRecienteDTO> recientes = new ArrayList<>();
        for (Cotizacion c : recientesRaw) {
            List<CotizacionDetalle> detalles = detalleRepo.findByCotizacion_IdCotizacion(c.getIdCotizacion());
            double totalCot = detalles.stream().mapToDouble(d -> d.getPrecioVentaSugerido() != null ? d.getPrecioVentaSugerido().doubleValue() : 0).sum();
            String familiaDet = detalles.isEmpty() || detalles.get(0).getCategoriaProducto() == null ? "Varios" : detalles.get(0).getCategoriaProducto().getNombre();

            recientes.add(CotizacionRecienteDTO.builder()
                    .idCotizacion(c.getIdCotizacion())
                    .numeroCotizacion(c.getNumeroCotizacion())
                    .fechaCotizacion(c.getFechaCotizacion())
                    .clienteNombre(c.getCliente() != null ? c.getCliente().getRazonSocial() : "Desconocido")
                    .estado(c.getEstado())
                    .familia(familiaDet)
                    .total(totalCot)
                    .motivo(
                            "RECHAZADA".equalsIgnoreCase(c.getEstado()) ? c.getObservaciones() : null)
                    .build());
        }

        return ReportesDTO.builder()
                .totalCotizaciones(total)
                .totalAprobadas(aprobadas)
                .totalRechazadas(rechazadas)
                .tasaConversion(Math.round(tasa * 10.0) / 10.0)
                .porMes(porMes)
                .motivosRechazo(motivos)
                .productosMasPersonalizados(productos)
                .caracteristicasMasSolicitadas(caracteristicas)
                .ultimasCotizaciones(recientes)
                .build();
    }

    /**
     * Traduce el periodo del reporte a una fecha de inicio.
     * Valores aceptados: 3m (por defecto), 6m, anio.
     */
    private LocalDateTime calcularFechaInicio(String periodo) {
        LocalDate hoy = LocalDate.now();
        String p = periodo == null ? "" : periodo.trim().toLowerCase();

        switch (p) {
            case "6m":
                return hoy.minusMonths(6).atStartOfDay();
            case "anio":
                return hoy.withDayOfYear(1).atStartOfDay();
            case "3m":
            default:
                return hoy.minusMonths(3).atStartOfDay();
        }
    }
}
