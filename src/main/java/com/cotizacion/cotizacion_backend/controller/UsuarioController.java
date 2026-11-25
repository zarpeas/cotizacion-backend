package com.cotizacion.cotizacion_backend.controller;

import com.cotizacion.cotizacion_backend.dto.UsuarioRequestDTO;
import com.cotizacion.cotizacion_backend.dto.UsuarioResponseDTO;
import com.cotizacion.cotizacion_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return usuarioService.listarUsuarios();
    }

    @PostMapping
    public UsuarioResponseDTO crear(@RequestBody UsuarioRequestDTO dto) {
        return usuarioService.crearUsuario(dto);
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO obtenerPorId(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id);
    }
    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizar(@PathVariable Integer id, @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.actualizarUsuario(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public void cambiarEstado(@PathVariable Integer id, @RequestParam Boolean activo) {
        usuarioService.cambiarEstado(id, activo);
    }
}