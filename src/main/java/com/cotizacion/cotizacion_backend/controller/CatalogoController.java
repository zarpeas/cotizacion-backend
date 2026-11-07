package com.cotizacion.cotizacion_backend.controller;

import com.cotizacion.cotizacion_backend.entity.*;
import com.cotizacion.cotizacion_backend.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@CrossOrigin(origins = "http://localhost:4200")

public class CatalogoController {
    @Autowired
    private CatalogoService catalogoService;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaProducto>> listarCategorias() {
        return ResponseEntity.ok(catalogoService.obtenerCategoriasActivas());
    }

    @GetMapping("/color")
    public ResponseEntity<List<Color>> listarColor() {
        return ResponseEntity.ok(catalogoService.obtenerColorActivas());
    }

    @GetMapping("/cerraduras")
    public ResponseEntity<List<Cerradura>> listarCerraduras() {
        return ResponseEntity.ok(catalogoService.obtenerCerradurasActivas());
    }

    @GetMapping("/bases")
    public ResponseEntity<List<Base>> listarBases() {
        return ResponseEntity.ok(catalogoService.obtenerBasesActivas());
    }

    @GetMapping("/tipopuerta")
    public ResponseEntity<List<TipoPuerta>> TipoPuerta() {
        return ResponseEntity.ok(catalogoService.obtenerTipoPuertaActivas());
    }

    @GetMapping("/espesor")
    public ResponseEntity<List<Espesor>> Espesor() {
        return ResponseEntity.ok(catalogoService.obtenerEspesorActivas());
    }


}
