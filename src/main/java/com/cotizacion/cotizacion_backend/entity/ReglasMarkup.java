package com.cotizacion.cotizacion_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reglas_markup")
public class ReglasMarkup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reglas_markup")
    private Integer idReglasMarkup;

    @Column(name = "cantidad_min")
    private Integer cantidadMin;

    @Column(name = "cantidad_max")
    private Integer cantidadMax;

    @Column(name = "markup", precision = 4, scale = 2)
    private BigDecimal markup;

    @Column(name = "aplica_descuento")
    private Boolean aplicaDescuento;

    @Column(name = "descuento_min_pct", precision = 4, scale = 2)
    private BigDecimal descuentoMinPct;

    @Column(name = "descuento_max_pct", precision = 4, scale = 2)
    private BigDecimal descuentoMaxPct;

    @Column(name = "estado")
    private Boolean estado;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) {
            this.estado = true;
        }
        if (this.aplicaDescuento == null) {
            this.aplicaDescuento = false;
        }
    }
}