package com.cotizacion.cotizacion_backend.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias_producto")

public class CategoriaProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorias_producto")
    private Integer id_categorias_producto;

    @Column(name = "nombre", nullable = false, length = 40, unique = true)
    private String nombre;

    @Column(name = "estado")
    private Boolean estado = true;
}



