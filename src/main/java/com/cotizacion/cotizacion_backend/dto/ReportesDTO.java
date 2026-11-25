package com.cotizacion.cotizacion_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ReportesDTO {
    private Long totalCotizaciones;
    private Long totalAprobadas;
    private Long totalRechazadas;
    private Double tasaConversion;

    // Datos para gráficos
    private List<MesReporteDTO> porMes;
    private List<MotivoRechazoDTO> motivosRechazo;

    // Datos para tablas de ranking
    private List<RankingDTO> productosMasPersonalizados;
    private List<RankingDTO> caracteristicasMasSolicitadas;

    // Últimas cotizaciones (reutilizamos tu DTO existente)
    private List<CotizacionRecienteDTO> ultimasCotizaciones;

    @Data @Builder
    public static class MesReporteDTO {
        private String mes;
        private Long ganadas;
        private Long rechazadas;
    }

    @Data @Builder
    public static class MotivoRechazoDTO {
        private String motivo;
        private Long cantidad;
        private Double porcentaje;
    }

    @Data @Builder
    public static class RankingDTO {
        private String nombre;
        private Long cantidad;
        private Double porcentaje;
    }
}