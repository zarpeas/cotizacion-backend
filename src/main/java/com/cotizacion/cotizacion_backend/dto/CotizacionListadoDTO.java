package com.cotizacion.cotizacion_backend.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CotizacionListadoDTO {
    private Integer idCotizacion;
    private String numeroCotizacion;
    private LocalDateTime fechaCotizacion;
    private String clienteNombre;
    private String productoFamilia;
    private BigDecimal montoTotal;
    private String estado;
    private String motivo;

    public CotizacionListadoDTO() {}


    public CotizacionListadoDTO(Integer idCotizacion, String numeroCotizacion, LocalDateTime fechaCotizacion,
                             String clienteNombre, String productoFamilia, BigDecimal montoTotal,
                             String estado, String motivo) {
        this.idCotizacion = idCotizacion;
        this.numeroCotizacion = numeroCotizacion;
        this.fechaCotizacion = fechaCotizacion;
        this.clienteNombre = clienteNombre;
        this.productoFamilia = productoFamilia;
        this.montoTotal = montoTotal;
        this.estado = estado;
        this.motivo = motivo;
    }


}