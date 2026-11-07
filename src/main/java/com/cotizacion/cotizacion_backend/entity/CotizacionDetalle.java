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
@Table(name = "cotizacion_detalle")
public class CotizacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion_detalle")
    private Integer idCotizacionDetalle;

    // --- RELACIONES (Llaves Foráneas) ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cotizacion", nullable = false)
    private Cotizacion cotizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categorias_producto", nullable = false)
    private CategoriaProducto categoriaProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cerradura")
    private Cerradura cerradura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_base")
    private Base base;


    @Column(name = "id_espesor")
    private Integer idEspesor;

    @Column(name = "id_color")
    private Integer idColor;

    @Column(name = "id_tipo_puerta")
    private Integer idTipoPuerta;

    @Column(name = "id_reglas_markup")
    private Integer idReglasMarkup;

    // --- CARACTERÍSTICAS DEL MUEBLE ---

    @Column(name = "familia_producto", length = 50)
    private String familia_producto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "alto_cm", precision = 6, scale = 1)
    private BigDecimal altoCm;

    @Column(name = "ancho_cm", precision = 6, scale = 1)
    private BigDecimal anchoCm;

    @Column(name = "fondo_cm", precision = 6, scale = 1)
    private BigDecimal fondoCm;

    @Column(name = "cantidad_puertas")
    private Integer cantidadPuertas;

    @Column(name = "cantidad_bandejas")
    private Integer cantidadBandejas;

    @Column(name = "techo_inclinado")
    private Boolean techoInclinado;

    // --- RESULTADOS DEL MACHINE LEARNING Y MARKUP ---

    @Column(name = "costo_fabricacion", precision = 12, scale = 2)
    private BigDecimal costoFabricacion;

    @Column(name = "confianza_ml", precision = 5, scale = 2)
    private BigDecimal confianzaMl;

    @Column(name = "version_modelo_ml", length = 30)
    private String versionModeloMl;

    @Column(name = "markup_aplicado", precision = 4, scale = 2)
    private BigDecimal markupAplicado;

    @Column(name = "precio_venta_sugerido", precision = 12, scale = 2)
    private BigDecimal precioVentaSugerido;

    @Column(name = "aplica_descuento")
    private Boolean aplicaDescuento;

    @Column(name = "descuento_sugerido_min", precision = 4, scale = 2)
    private BigDecimal descuentoSugeridoMin;

    @Column(name = "descuento_sugerido_max", precision = 4, scale = 2)
    private BigDecimal descuentoSugeridoMax;
}