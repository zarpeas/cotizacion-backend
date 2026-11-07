package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.entity.*;
import com.cotizacion.cotizacion_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogoService {
    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private CategoriaProductoRepository categoriaRepository;

    @Autowired
    private CerraduraRepository cerraduraRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private TipoPuertaRepository tipoPuertaRepository;

    @Autowired
    private EspesorRepository espesorRepository;

    public List<Base> obtenerBasesActivas() {
        return baseRepository.findByEstadoTrue();
    }

    public List<CategoriaProducto> obtenerCategoriasActivas() {
        return categoriaRepository.findByEstadoTrue();
    }

    public List<Cerradura> obtenerCerradurasActivas() {
        return cerraduraRepository.findByEstadoTrue();
    }

    public List<Color> obtenerColorActivas() {
        return colorRepository.findByEstadoTrue();
    }

    public List<TipoPuerta> obtenerTipoPuertaActivas() {
        return tipoPuertaRepository.findByEstadoTrue();
    }
    public List<Espesor> obtenerEspesorActivas() {
        return espesorRepository.findByEstadoTrue();
    }
}
