package com.cotizacion.cotizacion_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PrediccionResponseDTO {

    @JsonProperty("costo_fabricacion_unitario")
    private BigDecimal costoFabricacionUnitario;

    @JsonProperty("mape_esperado_pct")
    private BigDecimal mapeEsperadoPct;

    @JsonProperty("mape_mediana_pct")
    private BigDecimal mapeMedianaPct;

    @JsonProperty("kg_total_usado")
    private BigDecimal kgTotalUsado;

    @JsonProperty("kg_total_fuente")
    private String kgTotalFuente;

    @JsonProperty("modelo")
    private String modelo;

    @JsonProperty("confianza_r2")
    private BigDecimal confianzaR2;
}