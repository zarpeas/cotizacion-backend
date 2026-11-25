package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Fachada estable para los controladores. La lógica real está separada en:
 * consultas, escrituras y cálculo de precios/ML.
 */
@Service
public class CotizacionService {

    @Autowired private CotizacionConsultaService consulta;
    @Autowired private CotizacionComandoService comando;
    @Autowired private CotizacionCalculoService calculo;

    private static final int MAX_REINTENTOS_NUMERO = 3;

    public Page<CotizacionListadoDTO> listarCotizaciones(String search, String estado, Pageable pageable) {
        return consulta.listarCotizaciones(search, estado, pageable);
    }

    public CotizacionResponseDTO procesarCotizacion(MuebleInputDTO input) {
        return calculo.respuesta(calculo.calcular(input, true));
    }

    public CotizacionResponseDTO procesarYGuardarCotizacion(MuebleInputDTO input, Integer idCliente, Integer idUsuario) {
        for (int intento = 1; intento <= MAX_REINTENTOS_NUMERO; intento++) {
            try {
                return comando.guardar(input, idCliente, idUsuario);
            } catch (DataIntegrityViolationException e) {
                if (!esNumeroDuplicado(e) || intento == MAX_REINTENTOS_NUMERO) {
                    throw e;
                }
            }
        }
        throw new IllegalStateException("No se pudo generar un número de cotización único");
    }

    private boolean esNumeroDuplicado(DataIntegrityViolationException e) {
        Throwable causa = e.getMostSpecificCause();
        String mensaje = causa != null ? causa.getMessage() : null;
        return mensaje != null && mensaje.contains("uq_cotizaciones_numero");
    }

    public void cancelarCotizacion(Integer id, String motivo) {
        comando.cancelar(id, motivo);
    }

    public CotizacionListadoDTO obtenerCotizacionPorId(Integer id) {
        return consulta.obtenerCotizacionPorId(id);
    }

    public CotizacionResponseDTO actualizarCotizacion(Integer idCotizacion, MuebleInputDTO input) {
        return comando.actualizar(idCotizacion, input);
    }

    public CotizacionDetalleDTO obtenerCotizacionDetalle(Integer id) {
        return consulta.obtenerCotizacionDetalle(id);
    }

    public CotizacionDetalleDTO reestimarCotizacion(Integer id, CotizacionReestimacionDTO input) {
        return comando.reestimar(id, input);
    }

    public void aceptarCotizacion(Integer id) {
        comando.aceptar(id);
    }
}
