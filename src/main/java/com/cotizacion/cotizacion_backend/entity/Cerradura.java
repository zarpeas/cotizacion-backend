package com.cotizacion.cotizacion_backend.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cerradura")

public class Cerradura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cerradura")
    private Integer id_cerradura;

    @Column(name = "nombre", nullable = false, length = 40, unique = true)
    private String nombre;

    @Column(name = "estado")
    private Boolean estado = true;
}
