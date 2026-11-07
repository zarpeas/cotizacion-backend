package com.cotizacion.cotizacion_backend.repository;


import com.cotizacion.cotizacion_backend.entity.TipoPuerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoPuertaRepository extends JpaRepository<TipoPuerta, Integer> {
    List<TipoPuerta> findByEstadoTrue();
}
