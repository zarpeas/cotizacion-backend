package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Integer> {
    List<CategoriaProducto> findByEstadoTrue();
}