package com.cotizacion.cotizacion_backend.dto;

import java.time.LocalDateTime;

public class ClienteResponse {
    private Integer idCliente;
    private String razonSocial;
    private String rut;
    private String telefono;
    private String correo;
    private String direccion;
    private String ciudad;
    private String comuna;
    private Boolean estado;
    private LocalDateTime fechaCreacion;

    public ClienteResponse() {}

    public ClienteResponse(Integer idCliente, String razonSocial, String rut,
                           String telefono, String correo, String direccion,
                           String ciudad, String comuna, Boolean estado, LocalDateTime fechaCreacion) {
        this.idCliente = idCliente;
        this.razonSocial = razonSocial;
        this.rut = rut;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.comuna = comuna;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getRut() { return rut; }
    public void setRUT(String rut) { this.rut = rut; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}