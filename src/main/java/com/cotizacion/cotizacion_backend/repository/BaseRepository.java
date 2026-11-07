package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BaseRepository extends JpaRepository<Base, Integer> {
    List<Base> findByEstadoTrue();
}