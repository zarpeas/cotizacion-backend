package com.cotizacion.cotizacion_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponseDTO {
    private Integer idUsuario;
    private String nombres;
    private String apellidos;
    private String username;
    private String rol;
    private Boolean estado;
    private String correo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntrada;
}
