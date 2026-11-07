package com.cotizacion.cotizacion_backend.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MesDataDTO {
    private String mes;
    private Integer cotizaciones;
    private Integer aceptadas;
}