package com.cotizacion.cotizacion_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "historial_estados_cotizacion")
public class HistorialEstadosCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial_estados")
    private Long id_historial_estados;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cotizacion", nullable = false)
    private Cotizacion cotizacion;


    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoCotizacion estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false)
    private EstadoCotizacion estadoNuevo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "fecha", updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
    }

    enum EstadoCotizacion {
        Pendiente,
        Aceptada,
        Cancelada
    }
}