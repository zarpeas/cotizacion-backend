package com.cotizacion.cotizacion_backend.dto;

public class LoginResponse {
    private Integer  idUsuario;
    private String nombres;
    private String apellidos;
    private String username;
    private String rol;

    public LoginResponse() {
    }

    public LoginResponse(
            Integer  idUsuario,
            String nombres,
            String apellidos,
            String username,
            String rol) {

        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.username = username;
        this.rol = rol;
    }

    public Integer  getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer  idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
