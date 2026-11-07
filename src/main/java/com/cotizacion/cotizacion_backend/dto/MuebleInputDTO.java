package com.cotizacion.cotizacion_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MuebleInputDTO {

    // IDs de catálogos
    @NotNull private Integer idCategoriaProducto;
    @NotNull private Integer idBase;
    @NotNull private Integer idCerradura;
    @NotNull private Integer idTipoPuerta;
    @NotNull private Integer idEspesor;

    // Dimensiones
    @NotNull private Double alto;
    @NotNull private Double ancho;
    @NotNull private Double fondo;

    // Cantidades y Características
    private Integer cantidad;
    private Integer cantidadPuertas;
    private Integer cantidadBandejas;
    private Boolean techoInclinado;

    // Datos extra para el ML
    private Double kgTotal;
    private Integer divisiones;

    private Integer idColor;
    private String codigoFamilia;
}