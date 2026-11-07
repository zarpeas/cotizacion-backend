package com.cotizacion.cotizacion_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "RUT", length = 20)
    private String rut;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "correo", length = 120)
    private String correo;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "comuna", length = 100)
    private String comuna;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;


    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cotizacion> cotizaciones;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) this.estado = true;
        this.fechaCreacion = LocalDateTime.now();
    }
}