package com.cotizacion.cotizacion_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "color")
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_color")
    private Integer id_color;

    @Column(name = "nombre", nullable = false, length = 40, unique = true)
    private String nombre;

    @Column(name = "codigo_hex", nullable = false, length = 7)
    private String codigo_hex;

    @Column(name = "estado")
    private Boolean estado = true;
}
