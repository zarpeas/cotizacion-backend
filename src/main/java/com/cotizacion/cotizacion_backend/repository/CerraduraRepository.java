package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.Cerradura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CerraduraRepository extends JpaRepository<Cerradura, Integer> {
    List<Cerradura> findByEstadoTrue();
}