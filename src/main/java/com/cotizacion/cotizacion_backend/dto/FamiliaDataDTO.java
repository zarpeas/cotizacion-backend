package com.cotizacion.cotizacion_backend.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FamiliaDataDTO {
    private String familia;
    private Integer cantidad;
}