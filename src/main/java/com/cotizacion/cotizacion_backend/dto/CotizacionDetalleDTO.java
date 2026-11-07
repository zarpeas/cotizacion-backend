package com.cotizacion.cotizacion_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CotizacionDetalleDTO {
    private Integer idCotizacion;
    private String numeroCotizacion;
    private LocalDateTime fechaCotizacion;
    private String estado;
    private String observaciones;

    // Cliente
    private String clienteRazonSocial;
    private String clienteRut;
    private String clienteCorreo;
    private String clienteTelefono;

    // Producto
    private String categoria;
    private Integer cantidad;
    private BigDecimal altoCm;
    private BigDecimal anchoCm;
    private BigDecimal fondoCm;
    private String espesor;
    private String color;
    private String cerradura;
    private String base;
    private String tipoPuerta;
    private Integer cantidadPuertas;
    private Integer cantidadBandejas;
    private Boolean techoInclinado;
    private String codiFamilia;

    // Finanzas (ML)
    private BigDecimal costoFabricacion;
    private BigDecimal markupAplicado;
    private BigDecimal confianzaMl;
    private BigDecimal precioVentaSugerido;
    //private BigDecimal montoTotal;
}