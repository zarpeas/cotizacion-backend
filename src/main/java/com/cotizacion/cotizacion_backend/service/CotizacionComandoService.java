package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.*;
import com.cotizacion.cotizacion_backend.entity.*;
import com.cotizacion.cotizacion_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/** Escrituras de cotizaciones: guardar, actualizar, reestimar, cancelar y aceptar. */
@Service
public class CotizacionComandoService {

    @Autowired private CotizacionCalculoService calculo;
    @Autowired private CotizacionConsultaService consulta;
    @Autowired private CotizacionRepository cotizacionRepo;
    @Autowired private CotizacionDetalleRepository detalleRepo;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private ReglasMarkupRepository reglasMarkupRepo;

    @Transactional
    public CotizacionResponseDTO guardar(MuebleInputDTO input, Integer idCliente, Integer idUsuario) {
        CotizacionCalculoService.Resultado resultado = calculo.calcular(input, false);

        CategoriaProducto categoria = resultado.categoria();
        Cerradura cerradura = resultado.cerradura();
        Base base = resultado.base();
        ReglasMarkup regla = resultado.regla();
        PrediccionResponseDTO responseML = resultado.prediccion();
        // Si el wizard ya calculó y mostró los montos, se guardan esos valores
        // exactos en vez de los que devuelve un segundo llamado al ML.
        BigDecimal costoFab = input.getCostoFabricacionUnitario() != null
                ? input.getCostoFabricacionUnitario().setScale(0, RoundingMode.HALF_UP)
                : resultado.costoFabricacion();
        BigDecimal precioVentaSugerido = input.getPrecioVentaSugerido() != null
                ? input.getPrecioVentaSugerido().setScale(0, RoundingMode.HALF_UP)
                : resultado.precioVentaSugerido();
        if (costoFab.signum() <= 0 || precioVentaSugerido.signum() <= 0) {
            throw new RuntimeException("El costo y el precio sugerido deben ser mayores a 0");
        }
        Integer nPuertas = resultado.nPuertas();
        Integer nCuerpos = resultado.nCuerpos();
        Integer cantidadMuebles = (input.getCantidad() != null) ? input.getCantidad() : 1;

        Cliente cliente = clienteRepo.findById(idCliente.longValue()).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Usuario usuario = usuarioRepo.findById(idUsuario.longValue()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCliente(cliente);
        cotizacion.setUsuario(usuario);
        cotizacion.setNumeroCotizacion(generarNumeroCotizacion());
        cotizacion.setFechaCotizacion(LocalDateTime.now());
        cotizacion.setEstado("Pendiente");

        Cotizacion cotizacionGuardada = cotizacionRepo.save(cotizacion);

        CotizacionDetalle detalle = new CotizacionDetalle();
        detalle.setCotizacion(cotizacionGuardada);
        detalle.setCategoriaProducto(categoria);
        String familiaAGuardar = (input.getCodigoFamilia() != null && !input.getCodigoFamilia().trim().isEmpty())
                ? input.getCodigoFamilia().trim()
                : categoria.getNombre();

        detalle.setFamilia_producto(familiaAGuardar);
        detalle.setCerradura(cerradura);
        detalle.setBase(base);
        detalle.setIdEspesor(input.getIdEspesor());
        detalle.setIdTipoPuerta(input.getIdTipoPuerta());
        detalle.setIdReglasMarkup(regla.getIdReglasMarkup());
        detalle.setCantidad(cantidadMuebles);
        detalle.setAltoCm(BigDecimal.valueOf(input.getAlto()));
        detalle.setAnchoCm(BigDecimal.valueOf(input.getAncho()));
        detalle.setFondoCm(BigDecimal.valueOf(input.getFondo()));

        detalle.setCantidadPuertas(nPuertas);
        detalle.setCantidadCasilleros(nPuertas == 0 ? nCuerpos : 0);

        detalle.setCantidadBandejas(input.getCantidadBandejas());
        detalle.setTechoInclinado(input.getTechoInclinado() != null && input.getTechoInclinado());
        detalle.setCostoFabricacion(costoFab);
        detalle.setConfianzaMl(responseML.getMapeEsperadoPct());
        detalle.setErrorPromedioMape(responseML.getMapeMedianaPct());
        detalle.setVersionModeloMl(responseML.getModelo());
        detalle.setMarkupAplicado(regla.getMarkup());
        detalle.setPrecioVentaSugerido(precioVentaSugerido);

        detalle.setAplicaDescuento(regla.getAplicaDescuento() != null ? regla.getAplicaDescuento() : false);
        detalle.setDescuentoSugeridoMin(regla.getDescuentoMinPct());
        detalle.setDescuentoSugeridoMax(regla.getDescuentoMaxPct());

        detalle.setIdColor(input.getIdColor() != null ? input.getIdColor() : 1);
        detalle.setIdEspesor(input.getIdEspesor());

        detalleRepo.save(detalle);

        CotizacionResponseDTO respuesta = calculo.respuesta(resultado);
        respuesta.setCostoFabricacionUnitario(costoFab);
        respuesta.setPrecioVentaSugerido(precioVentaSugerido);
        return respuesta;
    }

    @Transactional
    public CotizacionResponseDTO actualizar(Integer idCotizacion, MuebleInputDTO input) {
        Cotizacion cotizacion = cotizacionRepo.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        List<CotizacionDetalle> detalles = detalleRepo.findByCotizacion_IdCotizacion(idCotizacion);
        if (detalles.isEmpty()) {
            throw new RuntimeException("La cotización no tiene detalles para actualizar");
        }
        CotizacionDetalle detalle = detalles.get(0);

        CotizacionCalculoService.Resultado resultado = calculo.calcular(input, false);
        CategoriaProducto categoria = resultado.categoria();
        Cerradura cerradura = resultado.cerradura();
        Base base = resultado.base();
        ReglasMarkup regla = resultado.regla();
        PrediccionResponseDTO responseML = resultado.prediccion();
        BigDecimal costoFab = resultado.costoFabricacion();
        BigDecimal precioVentaSugerido = resultado.precioVentaSugerido();
        Integer nPuertas = resultado.nPuertas();
        Integer nCuerpos = resultado.nCuerpos();
        Integer cantidadMuebles = (input.getCantidad() != null) ? input.getCantidad() : 1;

        detalle.setCategoriaProducto(categoria);
        String familiaAGuardar = (input.getCodigoFamilia() != null && !input.getCodigoFamilia().trim().isEmpty())
                ? input.getCodigoFamilia().trim()
                : categoria.getNombre();

        detalle.setFamilia_producto(familiaAGuardar);
        detalle.setCerradura(cerradura);
        detalle.setBase(base);
        detalle.setIdEspesor(input.getIdEspesor());
        detalle.setIdTipoPuerta(input.getIdTipoPuerta());
        detalle.setIdReglasMarkup(regla.getIdReglasMarkup());

        detalle.setCantidad(cantidadMuebles);
        detalle.setAltoCm(input.getAlto() != null ? BigDecimal.valueOf(input.getAlto()) : null);
        detalle.setAnchoCm(input.getAncho() != null ? BigDecimal.valueOf(input.getAncho()) : null);
        detalle.setFondoCm(input.getFondo() != null ? BigDecimal.valueOf(input.getFondo()) : null);
        detalle.setCantidadPuertas(nPuertas);
        detalle.setCantidadCasilleros(nPuertas == 0 ? nCuerpos : 0);
        detalle.setCantidadBandejas(input.getCantidadBandejas());
        detalle.setTechoInclinado(input.getTechoInclinado() != null && input.getTechoInclinado());

        detalle.setCostoFabricacion(costoFab);
        detalle.setConfianzaMl(responseML.getMapeEsperadoPct());
        detalle.setErrorPromedioMape(responseML.getMapeMedianaPct());
        detalle.setVersionModeloMl(responseML.getModelo());
        detalle.setMarkupAplicado(regla.getMarkup());
        detalle.setPrecioVentaSugerido(precioVentaSugerido);

        detalle.setAplicaDescuento(regla.getAplicaDescuento() != null ? regla.getAplicaDescuento() : false);
        detalle.setDescuentoSugeridoMin(regla.getDescuentoMinPct());
        detalle.setDescuentoSugeridoMax(regla.getDescuentoMaxPct());

        detalleRepo.save(detalle);

        return calculo.respuesta(resultado);
    }

    /**
     * Reestima manualmente el costo de fabricación de una cotización pendiente.
     * El precio sugerido se recalcula con la regla de markup ya configurada y
     * la confianza del modelo pasa a 0% porque el valor ya no proviene del ML.
     */
    @Transactional
    public CotizacionDetalleDTO reestimar(Integer id, CotizacionReestimacionDTO input) {
        Cotizacion cotizacion = cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        if (!"Pendiente".equalsIgnoreCase(cotizacion.getEstado())) {
            throw new RuntimeException("Solo se puede reestimar una cotización en estado pendiente");
        }

        List<CotizacionDetalle> detalles = detalleRepo.findByCotizacion_IdCotizacion(id);
        if (detalles.isEmpty()) {
            throw new RuntimeException("La cotización no tiene detalles para reestimar");
        }
        CotizacionDetalle detalle = detalles.get(0);

        BigDecimal markup = detalle.getMarkupAplicado();
        if ((markup == null || markup.compareTo(BigDecimal.ZERO) <= 0)
                && detalle.getIdReglasMarkup() != null) {
            ReglasMarkup regla = reglasMarkupRepo.findById(detalle.getIdReglasMarkup()).orElse(null);
            if (regla != null && regla.getMarkup() != null) {
                markup = regla.getMarkup();
            }
        }
        if (markup == null || markup.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("No existe una regla de markup válida para esta cotización");
        }

        BigDecimal costo = input.getCostoFabricacion();
        if (costo == null || costo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El costo de fabricación debe ser mayor a 0");
        }

        BigDecimal precioSugerido = costo.multiply(markup).setScale(0, RoundingMode.HALF_UP);

        detalle.setCostoFabricacion(costo.setScale(0, RoundingMode.HALF_UP));
        detalle.setMarkupAplicado(markup);
        detalle.setPrecioVentaSugerido(precioSugerido);
        detalle.setConfianzaMl(BigDecimal.ZERO);
        detalleRepo.save(detalle);

        return consulta.obtenerCotizacionDetalle(id);
    }

    @Transactional
    public void cancelar(Integer id, String motivo) {
        Cotizacion cotizacion = cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        cotizacion.setEstado("RECHAZADA");
        cotizacion.setObservaciones(motivo);

        cotizacionRepo.save(cotizacion);
    }

    @Transactional
    public void aceptar(Integer id) {
        Cotizacion cotizacion = cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        cotizacion.setEstado("APROBADO");
        cotizacion.setObservaciones("Aprobada por el asesor comercial.");

        cotizacionRepo.save(cotizacion);
    }

    /** Siguiente número libre del año, basado en el mayor ya registrado. */
    private String generarNumeroCotizacion() {
        String prefijo = String.format("COT-%d", LocalDateTime.now().getYear());
        long correlativo = cotizacionRepo.findMaxCorrelativoPorPrefijo(prefijo).longValue() + 1;
        return String.format("%s-%05d", prefijo, correlativo);
    }
}
