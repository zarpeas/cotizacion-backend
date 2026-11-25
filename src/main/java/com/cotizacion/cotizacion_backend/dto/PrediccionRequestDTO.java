package com.cotizacion.cotizacion_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PrediccionRequestDTO {

    private String familia;
    private Double alto;
    private Double ancho;
    private Double fondo;
    private Double espesor;

    @JsonProperty("kg_total")
    private Double kgTotal;

    private String cerradura;
    private String base;

    @JsonProperty("techo_inclinado")
    private Integer techoInclinado;

    @JsonProperty("n_puertas")
    private Integer nPuertas;

    @JsonProperty("tipo_puerta")
    private String tipoPuerta;

    private Integer bandejas;
    private Integer divisiones;

    @JsonProperty("producto_txt")
    private String productoTxt;

    private BigDecimal errorPromedioMape;
    @JsonProperty("n_cuerpos")
    private Integer nCuerpos;
}