package com.cotizacion.cotizacion_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CotizacionReestimacionDTO {

    @NotNull(message = "El costo de fabricación es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo de fabricación debe ser mayor a 0")
    private BigDecimal costoFabricacion;
}
