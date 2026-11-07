package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.Cotizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {

    @Query(value = "SELECT COUNT(*) FROM cotizacion WHERE MONTH(fecha_cotizacion) = MONTH(CURDATE()) AND YEAR(fecha_cotizacion) = YEAR(CURDATE())", nativeQuery = true)
    Long countCotizacionesMesActual();

    @Query(value = "SELECT DATE_FORMAT(fecha_cotizacion, '%b') as mes, " +
            "COUNT(id_cotizacion) as total, " +
            "SUM(CASE WHEN estado = 'Aceptada' THEN 1 ELSE 0 END) as aceptadas " +
            "FROM cotizacion " +
            "WHERE fecha_cotizacion >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
            "GROUP BY MONTH(fecha_cotizacion), DATE_FORMAT(fecha_cotizacion, '%b') " +
            "ORDER BY MONTH(fecha_cotizacion)", nativeQuery = true)
    List<Object[]> getEvolucionUltimos6Meses();

    List<Cotizacion> findTop5ByOrderByIdCotizacionDesc();

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
}