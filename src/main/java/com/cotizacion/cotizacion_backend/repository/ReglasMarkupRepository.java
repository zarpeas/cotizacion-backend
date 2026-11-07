package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.ReglasMarkup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReglasMarkupRepository extends JpaRepository<ReglasMarkup, Integer> {

    @Query("SELECT r FROM ReglasMarkup r " +
            "WHERE r.estado = true " +
            "AND r.cantidadMin <= :cantidad " +
            "AND (r.cantidadMax IS NULL OR r.cantidadMax >= :cantidad) " +
            "ORDER BY r.cantidadMin DESC")
    Optional<ReglasMarkup> findReglaByCantidad(@Param("cantidad") Integer cantidad);
}