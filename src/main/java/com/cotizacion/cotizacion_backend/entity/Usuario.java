package com.cotizacion.cotizacion_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    // CAMBIADO para que coincida con tu AuthService
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    // AGREGADO para que coincida con tu AuthService
    @Column(name = "apellidos", length = 100)
    private String apellidos;

    @Column(name = "correo", length = 120)
    private String correo;

    @Column(name = "rol", length = 30)
    private String rol;

    @Column(name = "estado")
    private Boolean estado;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Cotizacion> cotizaciones;

    @PrePersist
    protected void onCreate() {
        if (this.estado == null) this.estado = true;
    }
}