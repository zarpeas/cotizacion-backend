package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.*;
import com.cotizacion.cotizacion_backend.entity.Cotizacion;
import com.cotizacion.cotizacion_backend.entity.CotizacionDetalle;
import com.cotizacion.cotizacion_backend.repository.ClienteRepository;
import com.cotizacion.cotizacion_backend.repository.CotizacionDetalleRepository;
import com.cotizacion.cotizacion_backend.repository.CotizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired private ClienteRepository clienteRepo;
    @Autowired private CotizacionRepository cotizacionRepo;
    @Autowired private CotizacionDetalleRepository detalleRepo;

    public DashboardStatsDTO obtenerEstadisticas() {

        Long totalClientes = clienteRepo.count();
        Long totalCotizaciones = cotizacionRepo.count();
        Long cotizacionesMes = cotizacionRepo.countCotizacionesMesActual();

        Double ingresos = detalleRepo.sumPrecioSugerido();
        if (ingresos == null) ingresos = 0.0;

        Double promedio = detalleRepo.avgPrecioSugerido();
        if (promedio == null) promedio = 0.0;

        // Gráfico de barras
        List<Object[]> evolucionRaw = cotizacionRepo.getEvolucionUltimos6Meses();
        List<MesDataDTO> porMes = new ArrayList<>();
        for (Object[] obj : evolucionRaw) {
            porMes.add(MesDataDTO.builder()
                    .mes((String) obj[0])
                    .cotizaciones(((Number) obj[1]).intValue())
                    .aceptadas(((Number) obj[2]).intValue())
                    .build());
        }

        // Gráfico de dona
        List<Object[]> familiasRaw = detalleRepo.getDistribucionPorFamilia();
        List<FamiliaDataDTO> porFamilia = new ArrayList<>();
        for (Object[] obj : familiasRaw) {
            porFamilia.add(FamiliaDataDTO.builder()
                    .familia((String) obj[0])
                    .cantidad(((Number) obj[1]).intValue())
                    .build());
        }

        // Tabla de Cotizaciones Recientes
        List<Cotizacion> recientesRaw = cotizacionRepo.findTop5ByOrderByIdCotizacionDesc();
        List<CotizacionRecienteDTO> recientes = new ArrayList<>();

        for (Cotizacion c : recientesRaw) {

            List<CotizacionDetalle> detalles = detalleRepo.findByCotizacion_IdCotizacion(c.getIdCotizacion());
            double totalCotizacion = 0.0;
            String familiaPrincipal = "Varios";

            for (CotizacionDetalle d : detalles) {
                if (d.getPrecioVentaSugerido() != null) {
                    totalCotizacion += d.getPrecioVentaSugerido().doubleValue();
                }
                if (d.getCategoriaProducto() != null && !detalles.isEmpty() && familiaPrincipal.equals("Varios")) {
                    familiaPrincipal = d.getCategoriaProducto().getNombre();
                }
            }

            recientes.add(CotizacionRecienteDTO.builder()
                    .idCotizacion(c.getIdCotizacion())
                    .numeroCotizacion(c.getNumeroCotizacion())
                    .fechaCotizacion(c.getFechaCotizacion())
                    .estado(c.getEstado() != null ? c.getEstado() : "Pendiente")
                    .clienteNombre(c.getCliente() != null ? c.getCliente().getRazonSocial() : "Cliente Desconocido")
                    .familia(familiaPrincipal)
                    .total(totalCotizacion)
                    .build());
        }

        return DashboardStatsDTO.builder()
                .totalClientes(totalClientes)
                .totalCotizaciones(totalCotizaciones)
                .cotizacionesMes(cotizacionesMes)
                .cotizacionesPendientes(0L)
                .ingresosProyectados(ingresos)
                .precioPromedio(promedio)
                .cotizacionesRecientes(recientes)
                .porMes(porMes)
                .porFamilia(porFamilia)
                .build();
    }
}