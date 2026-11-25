package com.cotizacion.cotizacion_backend.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String nombres;
    private String apellidos;
    private String username;
    private String password;
    private String rol;
    private Boolean estado;
    private String correo;
}