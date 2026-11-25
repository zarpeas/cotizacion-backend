package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.Cotizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {

    @Query(value = "SELECT COUNT(*) FROM cotizacion WHERE MONTH(fecha_cotizacion) = MONTH(CURDATE()) AND YEAR(fecha_cotizacion) = YEAR(CURDATE())", nativeQuery = true)
    Long countCotizacionesMesActual();

    @Query(value = "SELECT DATE_FORMAT(fecha_cotizacion, '%b') as mes, " +
            "COUNT(id_cotizacion) as total, " +
            // 👇 CAMBIA ESTA LÍNEA
            "SUM(CASE WHEN UPPER(estado) IN ('APROBADO', 'ACEPTADA') THEN 1 ELSE 0 END) as aceptadas " +
            "FROM cotizacion " +
            "WHERE fecha_cotizacion >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
            "GROUP BY MONTH(fecha_cotizacion), DATE_FORMAT(fecha_cotizacion, '%b') " +
            "ORDER BY MONTH(fecha_cotizacion)", nativeQuery = true)
    List<Object[]> getEvolucionUltimos6Meses();

    List<Cotizacion> findTop5ByOrderByIdCotizacionDesc();

    /**
     * Correlativo máximo del año en curso. Se usa en vez de count()+1 porque
     * los borrados dejan huecos y el conteo puede colisionar con un número existente.
     */
    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING_INDEX(numero_cotizacion, '-', -1) AS UNSIGNED)), 0) " +
            "FROM cotizacion " +
            "WHERE numero_cotizacion LIKE CONCAT(:prefijo, '-%')", nativeQuery = true)
    Number findMaxCorrelativoPorPrefijo(@Param("prefijo") String prefijo);

    // ESTA ES LA CONSULTA QUE USAREMOS PARA EL LISTADO CON FILTROS
    @Query(value = "SELECT DISTINCT c FROM Cotizacion c " +
            "LEFT JOIN c.cliente cli " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "LOWER(c.numeroCotizacion) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(cli.razonSocial) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:estado IS NULL OR :estado = '' OR c.estado = :estado) " +
            "ORDER BY c.fechaCotizacion DESC",
            countQuery = "SELECT COUNT(DISTINCT c) FROM Cotizacion c " +
                    "LEFT JOIN c.cliente cli " +
                    "WHERE (:search IS NULL OR :search = '' OR " +
                    "LOWER(c.numeroCotizacion) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                    "LOWER(cli.razonSocial) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                    "AND (:estado IS NULL OR :estado = '' OR c.estado = :estado)")
    Page<Cotizacion> buscarConFiltros(@Param("search") String search,
                                      @Param("estado") String estado,
                                      Pageable pageable);

    // 1. Conteo por estado
    long countByEstado(String estado);

    // 2. Conteo agrupado por mes (Ganadas vs Rechazadas)
    @Query(value = "SELECT DATE_FORMAT(c.fecha_cotizacion, '%b') as mes, " +
            "SUM(CASE WHEN UPPER(c.estado) IN ('APROBADO', 'ACEPTADA') THEN 1 ELSE 0 END) as ganadas, " +
            "SUM(CASE WHEN UPPER(c.estado) IN ('RECHAZADA', 'CANCELADA') THEN 1 ELSE 0 END) as rechazadas " +
            "FROM cotizacion c " +
            "WHERE c.fecha_cotizacion >= :fechaInicio " +
            "AND (:estado = '' OR UPPER(c.estado) = UPPER(:estado)) " +
            "AND (:familia = '' OR EXISTS (SELECT 1 FROM cotizacion_detalle cd " +
            "  JOIN categorias_producto cp ON cd.id_categorias_producto = cp.id_categorias_producto " +
            "  WHERE cd.id_cotizacion = c.id_cotizacion AND cp.nombre = :familia)) " +
            "GROUP BY MONTH(c.fecha_cotizacion), DATE_FORMAT(c.fecha_cotizacion, '%b') " +
            "ORDER BY MONTH(c.fecha_cotizacion)", nativeQuery = true)
    List<Object[]> getReporteMensual(@Param("estado") String estado,
                                     @Param("familia") String familia,
                                     @Param("fechaInicio") LocalDateTime fechaInicio);

    // 3. Motivos de rechazo más frecuentes
    @Query(value = "SELECT c.observaciones as motivo, COUNT(*) as cantidad " +
            "FROM cotizacion c " +
            "WHERE c.fecha_cotizacion >= :fechaInicio " +
            "AND UPPER(c.estado) IN ('RECHAZADA', 'CANCELADA') " +
            "AND (:estado = '' OR UPPER(c.estado) = UPPER(:estado)) " +
            "AND (:familia = '' OR EXISTS (SELECT 1 FROM cotizacion_detalle cd " +
            "  JOIN categorias_producto cp ON cd.id_categorias_producto = cp.id_categorias_producto " +
            "  WHERE cd.id_cotizacion = c.id_cotizacion AND cp.nombre = :familia)) " +
            "AND c.observaciones IS NOT NULL " +
            "AND c.observaciones != '' " +
            "AND c.observaciones NOT LIKE '%Aceptada por el asesor%' " +
            "GROUP BY c.observaciones " +
            "ORDER BY cantidad DESC LIMIT 5", nativeQuery = true)
    List<Object[]> getMotivosRechazo(@Param("estado") String estado,
                                     @Param("familia") String familia,
                                     @Param("fechaInicio") LocalDateTime fechaInicio);

    @Query(value = "SELECT COUNT(DISTINCT c.id_cotizacion) FROM cotizacion c " +
            "LEFT JOIN cotizacion_detalle cd ON c.id_cotizacion = cd.id_cotizacion " +
            "LEFT JOIN categorias_producto cp ON cd.id_categorias_producto = cp.id_categorias_producto " +
            "WHERE c.fecha_cotizacion >= :fechaInicio " +
            "AND (:estado = '' OR UPPER(c.estado) = UPPER(:estado)) " +
            "AND (:familia = '' OR cp.nombre = :familia)", nativeQuery = true)
    Long countConFiltro(@Param("estado") String estado,
                        @Param("familia") String familia,
                        @Param("fechaInicio") LocalDateTime fechaInicio);

    // Últimas cotizaciones aplicando los mismos filtros del reporte
    @Query(value = "SELECT c.* FROM cotizacion c " +
            "WHERE c.fecha_cotizacion >= :fechaInicio " +
            "AND (:estado = '' OR UPPER(c.estado) = UPPER(:estado)) " +
            "AND (:familia = '' OR EXISTS (SELECT 1 FROM cotizacion_detalle cd " +
            "  JOIN categorias_producto cp ON cd.id_categorias_producto = cp.id_categorias_producto " +
            "  WHERE cd.id_cotizacion = c.id_cotizacion AND cp.nombre = :familia)) " +
            "ORDER BY c.id_cotizacion DESC", nativeQuery = true)
    List<Cotizacion> findRecientesConFiltros(@Param("estado") String estado,
                                             @Param("familia") String familia,
                                             @Param("fechaInicio") LocalDateTime fechaInicio,
                                             Pageable pageable);
}