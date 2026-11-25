package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.CotizacionDetalleDTO;
import com.cotizacion.cotizacion_backend.dto.CotizacionListadoDTO;
import com.cotizacion.cotizacion_backend.entity.Cotizacion;
import com.cotizacion.cotizacion_backend.entity.CotizacionDetalle;
import com.cotizacion.cotizacion_backend.entity.Color;
import com.cotizacion.cotizacion_backend.entity.Espesor;
import com.cotizacion.cotizacion_backend.entity.TipoPuerta;
import com.cotizacion.cotizacion_backend.repository.CotizacionDetalleRepository;
import com.cotizacion.cotizacion_backend.repository.CotizacionRepository;
import com.cotizacion.cotizacion_backend.repository.ColorRepository;
import com.cotizacion.cotizacion_backend.repository.EspesorRepository;
import com.cotizacion.cotizacion_backend.repository.TipoPuertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** Lecturas de cotizaciones: listado, resumen por id y detalle. */
@Service
public class CotizacionConsultaService {

    @Autowired private CotizacionRepository cotizacionRepo;
    @Autowired private CotizacionDetalleRepository detalleRepo;
    @Autowired private EspesorRepository espesorRepo;
    @Autowired private ColorRepository colorRepo;
    @Autowired private TipoPuertaRepository tipoPuertaRepo;

    @Transactional(readOnly = true)
    public Page<CotizacionListadoDTO> listarCotizaciones(String search, String estado, Pageable pageable) {
        Page<Cotizacion> pageEntidades = cotizacionRepo.buscarConFiltros(search, estado, pageable);
        return pageEntidades.map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public CotizacionListadoDTO obtenerCotizacionPorId(Integer id) {
        Cotizacion c = cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        return mapToDTO(c);
    }

    @Transactional(readOnly = true)
    public CotizacionDetalleDTO obtenerCotizacionDetalle(Integer id) {
        Cotizacion c = cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        List<CotizacionDetalle> detalles = detalleRepo.findByCotizacion_IdCotizacion(id);
        if (detalles.isEmpty()) throw new RuntimeException("La cotización no tiene detalles");

        CotizacionDetalle d = detalles.get(0);

        String espesorStr = "N/A";
        if (d.getIdEspesor() != null) {
            Espesor esp = espesorRepo.findById(d.getIdEspesor()).orElse(null);
            if (esp != null) espesorStr = esp.getMilimetros() + " mm";
        }

        String colorStr = "N/A";
        if (d.getIdColor() != null) {
            Color col = colorRepo.findById(d.getIdColor()).orElse(null);
            if (col != null) colorStr = col.getNombre();
        }

        String tipoPuertaStr = "N/A";
        if (d.getIdTipoPuerta() != null) {
            TipoPuerta tp = tipoPuertaRepo.findById(d.getIdTipoPuerta()).orElse(null);
            if (tp != null) tipoPuertaStr = tp.getNombre();
        }

        return CotizacionDetalleDTO.builder()
                .idCotizacion(c.getIdCotizacion())
                .numeroCotizacion(c.getNumeroCotizacion())
                .fechaCotizacion(c.getFechaCotizacion())
                .estado(c.getEstado())
                .observaciones(c.getObservaciones())
                .clienteRazonSocial(c.getCliente() != null ? c.getCliente().getRazonSocial() : "N/A")
                .clienteRut(c.getCliente() != null ? c.getCliente().getRut() : "N/A")
                .clienteCorreo(c.getCliente() != null ? c.getCliente().getCorreo() : "N/A")
                .clienteTelefono(c.getCliente() != null ? c.getCliente().getTelefono() : "N/A")
                .categoria(d.getCategoriaProducto() != null ? d.getCategoriaProducto().getNombre() : "N/A")
                .cantidad(d.getCantidad())
                .altoCm(d.getAltoCm())
                .anchoCm(d.getAnchoCm())
                .fondoCm(d.getFondoCm())
                .espesor(espesorStr)
                .color(colorStr)
                .cerradura(d.getCerradura() != null ? d.getCerradura().getNombre() : "N/A")
                .base(d.getBase() != null ? d.getBase().getNombre() : "N/A")
                .tipoPuerta(tipoPuertaStr)
                .cantidadPuertas(d.getCantidadPuertas())
                .cantidadBandejas(d.getCantidadBandejas())
                .cantidadCasilleros(d.getCantidadCasilleros())
                .techoInclinado(d.getTechoInclinado())
                .costoFabricacion(d.getCostoFabricacion())
                .markupAplicado(d.getMarkupAplicado())
                .confianzaMl(d.getConfianzaMl())
                .errorPromedioMape(d.getErrorPromedioMape())
                .precioVentaSugerido(d.getPrecioVentaSugerido())
                .build();
    }

    private CotizacionListadoDTO mapToDTO(Cotizacion c) {
        CotizacionListadoDTO dto = new CotizacionListadoDTO();
        dto.setIdCotizacion(c.getIdCotizacion());
        dto.setNumeroCotizacion(c.getNumeroCotizacion());
        dto.setFechaCotizacion(c.getFechaCotizacion());
        dto.setClienteNombre(c.getCliente() != null ? c.getCliente().getRazonSocial() : "Cliente Desconocido");
        dto.setEstado(c.getEstado());
        dto.setMotivo("RECHAZADA".equalsIgnoreCase(c.getEstado()) ? c.getObservaciones() : null);

        List<CotizacionDetalle> detalles = detalleRepo.findByCotizacion_IdCotizacion(c.getIdCotizacion());

        if (detalles != null && !detalles.isEmpty()) {
            CotizacionDetalle primerDetalle = detalles.get(0);

            if (primerDetalle.getCategoriaProducto() != null) {
                dto.setProductoFamilia(primerDetalle.getCategoriaProducto().getNombre());
            } else {
                dto.setProductoFamilia(primerDetalle.getFamilia_producto() != null ? primerDetalle.getFamilia_producto() : "Sin detalle");
            }

            BigDecimal total = detalles.stream()
                    .map(d -> d.getPrecioVentaSugerido() != null ? d.getPrecioVentaSugerido() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setMontoTotal(total);
        } else {
            dto.setProductoFamilia("Sin detalle");
            dto.setMontoTotal(BigDecimal.ZERO);
        }
        return dto;
    }
}
