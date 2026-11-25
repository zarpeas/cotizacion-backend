package com.cotizacion.cotizacion_backend.controller;

import com.cotizacion.cotizacion_backend.dto.ReportesDTO;
import com.cotizacion.cotizacion_backend.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired private ReporteService reporteService;

    @GetMapping
    public ReportesDTO obtenerReporte(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String familia,
            @RequestParam(required = false, defaultValue = "3m") String periodo) {
        return reporteService.obtenerReporte(estado, familia, periodo);
    }
}