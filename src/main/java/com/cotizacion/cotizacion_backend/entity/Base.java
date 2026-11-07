package com.cotizacion.cotizacion_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "base")
public class Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_base")
    private Integer idBase;

    @Column(name = "nombre", nullable = false, length = 40, unique = true)
    private String nombre;

    @Column(name = "estado")
    private Boolean estado = true;
}