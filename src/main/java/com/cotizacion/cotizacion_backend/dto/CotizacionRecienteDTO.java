package com.cotizacion.cotizacion_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CotizacionRecienteDTO {
    private Integer idCotizacion;
    private String numeroCotizacion;
    private String familia;
    private LocalDateTime fechaCotizacion;
    private String estado;
    private String clienteNombre;
    private Double total;
    private Double precioSugerido;
    private String versionModelo;
}