package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.UsuarioRequestDTO;
import com.cotizacion.cotizacion_backend.dto.UsuarioResponseDTO;
import com.cotizacion.cotizacion_backend.entity.Usuario;
import com.cotizacion.cotizacion_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        if (usuarioRepo.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }
        if (usuarioRepo.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setUsername(dto.getUsername());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword())); // Encriptación
        usuario.setRol(dto.getRol());
        usuario.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        usuario.setCorreo(dto.getCorreo());
        usuario.setFecha_creacion(LocalDateTime.now());
        usuario.setFecha_entrada(LocalDateTime.now());

        Usuario guardado = usuarioRepo.save(usuario);
        return mapToDTO(guardado);
    }

    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepo.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getUsername().equals(dto.getUsername()) && usuarioRepo.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }
        if (!usuario.getCorreo().equals(dto.getCorreo()) && usuarioRepo.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setUsername(dto.getUsername());
        usuario.setRol(dto.getRol());
        usuario.setEstado(dto.getEstado());
        usuario.setCorreo(dto.getCorreo());


        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        return mapToDTO(usuarioRepo.save(usuario));
    }

    @Transactional
    public void cambiarEstado(Integer id, Boolean activo) {
        Usuario usuario = usuarioRepo.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado(activo);
        usuarioRepo.save(usuario);
    }

    private UsuarioResponseDTO mapToDTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .idUsuario(u.getIdUsuario())
                .nombres(u.getNombres())
                .apellidos(u.getApellidos())
                .username(u.getUsername())
                .rol(u.getRol())
                .estado(u.getEstado())
                .correo(u.getCorreo())
                .fechaCreacion(u.getFecha_creacion())
                .fechaEntrada(u.getFecha_entrada())
                .build();
    }
    public UsuarioResponseDTO obtenerPorId(Integer id) {
        Usuario usuario = usuarioRepo.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToDTO(usuario);
    }
}