package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.ClienteRequest;
import com.cotizacion.cotizacion_backend.dto.ClienteResponse;
import com.cotizacion.cotizacion_backend.entity.Cliente;
import com.cotizacion.cotizacion_backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ClienteResponse> listarActivos() {
        return clienteRepository.findByEstadoTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClienteResponse obtener(Long id) {
        Cliente c = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return toResponse(c);
    }

    public void eliminar(Long id) {
        Cliente c = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        c.setEstado(false);
        clienteRepository.save(c);
    }
    public ClienteResponse crear(ClienteRequest req) {
        if (req.getRazonSocial() == null || req.getRazonSocial().isBlank()) {
            throw new RuntimeException("La razón social es obligatoria");
        }
        if (req.getRut() != null && !req.getRut().isBlank()
                && clienteRepository.existsByRut(req.getRut())) {
            throw new RuntimeException("Ya existe un cliente con ese documento");
        }

        Cliente c = new Cliente();
        c.setRazonSocial(req.getRazonSocial());
        c.setRut(req.getRut());
        c.setTelefono(req.getTelefono());
        c.setCorreo(req.getCorreo());
        c.setDireccion(req.getDireccion());
        c.setCiudad(req.getCiudad());
        c.setComuna(req.getComuna());
        c.setEstado(true);

        return toResponse(clienteRepository.save(c));
    }

    public ClienteResponse actualizar(Long id, ClienteRequest req) {
        Cliente c = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (req.getRut() != null && !req.getRut().isBlank()
                && clienteRepository.existsByRutAndIdClienteNot(req.getRut(), id)) {
            throw new RuntimeException("Ya existe otro cliente con ese documento");
        }

        c.setRazonSocial(req.getRazonSocial());
        c.setRut(req.getRut());
        c.setTelefono(req.getTelefono());
        c.setCorreo(req.getCorreo());
        c.setDireccion(req.getDireccion());
        c.setCiudad(req.getCiudad());
        c.setComuna(req.getComuna());
        c.setEstado(req.getEstado() != null ? req.getEstado() : c.getEstado());
        return toResponse(clienteRepository.save(c));
    }

    private ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(
                c.getIdCliente(),
                c.getRazonSocial(),
                c.getRut(),
                c.getTelefono(),
                c.getCorreo(),
                c.getDireccion(),
                c.getCiudad(),
                c.getComuna(),
                c.getEstado(),
                c.getFechaCreacion()
        );
    }



}
