package com.cotizacion.cotizacion_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardStatsDTO {
    private Long totalClientes;
    private Long totalCotizaciones; // Antes era cotizacionesTotales
    private Long cotizacionesMes;
    private Long cotizacionesPendientes; // Nuevo
    private Double ingresosProyectados;
    private Double precioPromedio; // Nuevo

    private List<CotizacionRecienteDTO> cotizacionesRecientes; // Nuevo
    private List<MesDataDTO> porMes; // Antes era evolucion
    private List<FamiliaDataDTO> porFamilia; // Antes era familias
}