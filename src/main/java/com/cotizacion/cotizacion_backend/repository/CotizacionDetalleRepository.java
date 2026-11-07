package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.CotizacionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}