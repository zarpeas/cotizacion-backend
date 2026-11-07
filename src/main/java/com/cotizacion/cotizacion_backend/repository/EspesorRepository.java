package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.Espesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspesorRepository extends JpaRepository<Espesor, Integer> {
    List<Espesor> findByEstadoTrue();
}
