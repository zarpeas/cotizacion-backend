package com.cotizacion.cotizacion_backend.repository;

import com.cotizacion.cotizacion_backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByEstadoTrue();

    boolean existsByRut(String documento);

    boolean existsByRutAndIdClienteNot(String rut, Long idCliente);

}
