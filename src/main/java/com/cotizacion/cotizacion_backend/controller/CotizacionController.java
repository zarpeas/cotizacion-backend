package com.cotizacion.cotizacion_backend.controller;

import com.cotizacion.cotizacion_backend.dto.*;
import com.cotizacion.cotizacion_backend.service.CotizacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cotizacion")
@CrossOrigin(origins = "http://localhost:4200")
public class CotizacionController {

    @Autowired
    private CotizacionService cotizacionService;


    @PostMapping("/predecir")
    public ResponseEntity<CotizacionResponseDTO> predecirCosto(@Valid @RequestBody MuebleInputDTO input) {
        return ResponseEntity.ok(cotizacionService.procesarCotizacion(input));
    }


    @GetMapping("/listar")
    public ResponseEntity<Page<CotizacionListadoDTO>> listarCotizaciones(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CotizacionListadoDTO> result = cotizacionService.listarCotizaciones(search, estado, pageable);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CotizacionDetalleDTO> obtenerCotizacion(@PathVariable Integer id) {
        return ResponseEntity.ok(cotizacionService.obtenerCotizacionDetalle(id));
    }


    @PostMapping("/confirmar")
    public ResponseEntity<CotizacionResponseDTO> confirmarCotizacion(
            @RequestBody MuebleInputDTO input,
            @RequestParam Integer idCliente,
            @RequestParam Integer idUsuario) {

        CotizacionResponseDTO response = cotizacionService.procesarYGuardarCotizacion(input, idCliente, idUsuario);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CotizacionResponseDTO> actualizarCotizacion(
            @PathVariable Integer id,
            @RequestBody MuebleInputDTO input) {
        return ResponseEntity.ok(cotizacionService.actualizarCotizacion(id, input));
    }


    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarCotizacion(
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> body) {

        String motivo = (body != null && body.containsKey("motivo"))
                ? body.get("motivo")
                : "Cancelada por el usuario";

        cotizacionService.cancelarCotizacion(id, motivo);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/aceptar")
    public ResponseEntity<Void> aceptarCotizacion(@PathVariable Integer id) {
        cotizacionService.aceptarCotizacion(id);
        return ResponseEntity.ok().build();
    }
}