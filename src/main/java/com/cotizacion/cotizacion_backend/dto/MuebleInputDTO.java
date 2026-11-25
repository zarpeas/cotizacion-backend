package com.cotizacion.cotizacion_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

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

    // Valores ya calculados y mostrados en el frontend; se guardan tal cual al confirmar.
    private BigDecimal costoFabricacionUnitario;
    private BigDecimal precioVentaSugerido;

    // Cantidades y Características
    private Integer cantidad;
    private Integer cantidadPuertas;
    private Integer cantidadBandejas;
    private Integer cantidadCasilleros;
    private Boolean techoInclinado;

    // Datos extra para el ML
    private Double kgTotal;
    private Integer divisiones;

    private Integer idColor;
    private String codigoFamilia;
}