package com.cotizacion.cotizacion_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cotizacion")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion")
    private Integer idCotizacion;

    // ESTA ES LA PARTE CRÍTICA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "numero_cotizacion", length = 30)
    private String numeroCotizacion;

    @Column(name = "fecha_cotizacion")
    private LocalDateTime fechaCotizacion;

    @Column(name = "estado", length = 30)
    private String estado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}