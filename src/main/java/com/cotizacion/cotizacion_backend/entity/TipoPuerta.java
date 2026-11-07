package com.cotizacion.cotizacion_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_puerta")
public class TipoPuerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_puerta")
    private Integer id_tipo_puerta;

    @Column(name = "nombre", nullable = false, length = 40, unique = true)
    private String nombre;

    @Column(name = "estado")
    private Boolean estado = true;

}
