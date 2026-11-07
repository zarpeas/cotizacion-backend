package com.cotizacion.cotizacion_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CotizacionResponseDTO {
    private BigDecimal costoFabricacionUnitario;
    private BigDecimal precioVentaSugerido;
    private BigDecimal confianzaR2;
    private String modelo;
    private Boolean aplicaDescuento;
    private BigDecimal descuentoSugeridoMin;
    private BigDecimal descuentoSugeridoMax;
}