package com.cotizacion.cotizacion_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "espesor")
public class Espesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_espesor")
    private Integer id_espesor;

    @Column(name = "milimetros")
    private double milimetros;

    @Column(name = "estado")
    private Boolean estado = true;
}
