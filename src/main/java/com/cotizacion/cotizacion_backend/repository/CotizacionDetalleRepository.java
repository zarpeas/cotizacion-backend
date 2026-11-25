package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.CotizacionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CotizacionDetalleRepository extends JpaRepository<CotizacionDetalle, Integer> {

    @Query("SELECT COALESCE(SUM(c.precioVentaSugerido), 0) FROM CotizacionDetalle c")
    Double sumPrecioSugerido();

    @Query("SELECT COALESCE(AVG(c.precioVentaSugerido), 0) FROM CotizacionDetalle c")
    Double avgPrecioSugerido();


    List<CotizacionDetalle> findByCotizacion_IdCotizacion(Integer idCotizacion);

    @Query(value = "SELECT cat.nombre, COUNT(d.id_cotizacion_detalle) " +
            "FROM cotizacion_detalle d " +
            "JOIN categorias_producto cat ON d.id_categorias_producto = cat.id_categorias_producto " +
            "GROUP BY cat.nombre " +
            "ORDER BY COUNT(d.id_cotizacion_detalle) DESC", nativeQuery = true)
    List<Object[]> getDistribucionPorFamilia();

    // 1. Productos más vendidos
    @Query("SELECT d.categoriaProducto.nombre, SUM(d.cantidad) " +
            "FROM CotizacionDetalle d " +
            "WHERE d.cotizacion.fechaCotizacion >= :fechaInicio " +
            "AND (:estado = '' OR UPPER(d.cotizacion.estado) = UPPER(:estado)) " +
            "AND (:familia = '' OR d.categoriaProducto.nombre = :familia) " +
            "GROUP BY d.categoriaProducto.nombre " +
            "ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> getProductosMasVendidos(@Param("estado") String estado,
                                           @Param("familia") String familia,
                                           @Param("fechaInicio") LocalDateTime fechaInicio);

    // Consulta para obtener las características más solicitadas (Color, Cerradura, Base, Puerta, Espesor)
    @Query(value = "SELECT caracteristica, COUNT(*) as cantidad FROM ( " +
            "  SELECT col.nombre AS caracteristica FROM cotizacion_detalle cd " +
            "  INNER JOIN cotizacion c ON c.id_cotizacion = cd.id_cotizacion " +
            "  INNER JOIN categorias_producto cat ON cat.id_categorias_producto = cd.id_categorias_producto " +
            "  INNER JOIN color col ON col.id_color = cd.id_color " +
            "  WHERE c.fecha_cotizacion >= :fechaInicio " +
            "  AND (:estado = '' OR UPPER(c.estado) = UPPER(:estado)) " +
            "  AND (:familia = '' OR cat.nombre = :familia) " +
            "  UNION ALL " +
            "  SELECT cer.nombre AS caracteristica FROM cotizacion_detalle cd " +
            "  INNER JOIN cotizacion c ON c.id_cotizacion = cd.id_cotizacion " +
            "  INNER JOIN categorias_producto cat ON cat.id_categorias_producto = cd.id_categorias_producto " +
            "  INNER JOIN cerradura cer ON cer.id_cerradura = cd.id_cerradura " +
            "  WHERE c.fecha_cotizacion >= :fechaInicio " +
            "  AND (:estado = '' OR UPPER(c.estado) = UPPER(:estado)) " +
            "  AND (:familia = '' OR cat.nombre = :familia) " +
            "  UNION ALL " +
            "  SELECT bas.nombre AS caracteristica FROM cotizacion_detalle cd " +
            "  INNER JOIN cotizacion c ON c.id_cotizacion = cd.id_cotizacion " +
            "  INNER JOIN categorias_producto cat ON cat.id_categorias_producto = cd.id_categorias_producto " +
            "  INNER JOIN base bas ON bas.id_base = cd.id_base " +
            "  WHERE c.fecha_cotizacion >= :fechaInicio " +
            "  AND (:estado = '' OR UPPER(c.estado) = UPPER(:estado)) " +
            "  AND (:familia = '' OR cat.nombre = :familia) " +
            "  UNION ALL " +
            "  SELECT tp.nombre AS caracteristica FROM cotizacion_detalle cd " +
            "  INNER JOIN cotizacion c ON c.id_cotizacion = cd.id_cotizacion " +
            "  INNER JOIN categorias_producto cat ON cat.id_categorias_producto = cd.id_categorias_producto " +
            "  INNER JOIN tipo_puerta tp ON tp.id_tipo_puerta = cd.id_tipo_puerta " +
            "  WHERE c.fecha_cotizacion >= :fechaInicio " +
            "  AND (:estado = '' OR UPPER(c.estado) = UPPER(:estado)) " +
            "  AND (:familia = '' OR cat.nombre = :familia) " +
            "  UNION ALL " +
            "  SELECT CONCAT(esp.milimetros, ' mm') AS caracteristica FROM cotizacion_detalle cd " +
            "  INNER JOIN cotizacion c ON c.id_cotizacion = cd.id_cotizacion " +
            "  INNER JOIN categorias_producto cat ON cat.id_categorias_producto = cd.id_categorias_producto " +
            "  INNER JOIN espesor esp ON esp.id_espesor = cd.id_espesor " +
            "  WHERE c.fecha_cotizacion >= :fechaInicio " +
            "  AND (:estado = '' OR UPPER(c.estado) = UPPER(:estado)) " +
            "  AND (:familia = '' OR cat.nombre = :familia) " +
            ") AS sub " +
            "GROUP BY caracteristica " +
            "ORDER BY cantidad DESC LIMIT 7", nativeQuery = true)
    List<Object[]> getCaracteristicasMasSolicitadas(@Param("estado") String estado,
                                                    @Param("familia") String familia,
                                                    @Param("fechaInicio") LocalDateTime fechaInicio);
}