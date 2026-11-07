package com.cotizacion.cotizacion_backend.controller;

import com.cotizacion.cotizacion_backend.dto.ClienteRequest;
import com.cotizacion.cotizacion_backend.dto.ClienteResponse;
import com.cotizacion.cotizacion_backend.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        return ResponseEntity.ok(clienteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@RequestBody ClienteRequest req) {
        return ResponseEntity.ok(clienteService.crear(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable Long id,
                                                      @RequestBody ClienteRequest req) {
        return ResponseEntity.ok(clienteService.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
