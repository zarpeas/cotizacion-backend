package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.LoginRequest;
import com.cotizacion.cotizacion_backend.dto.LoginResponse;
import com.cotizacion.cotizacion_backend.entity.Usuario;
import com.cotizacion.cotizacion_backend.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Usuario o contraseña incorrectos"));

        if (!usuario.getEstado()) {
            throw new RuntimeException("El usuario se encuentra inactivo");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                usuario.getPasswordHash())) {

            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        return new LoginResponse(
                usuario.getIdUsuario(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getUsername(),
                usuario.getRol()
        );
    }
}
