package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.*;
import com.cotizacion.cotizacion_backend.entity.*;
import com.cotizacion.cotizacion_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CotizacionService {

    @Autowired private MLClientService mlClientService;
    @Autowired private BaseRepository baseRepo;
    @Autowired private CerraduraRepository cerraduraRepo;
    @Autowired private CategoriaProductoRepository categoriaRepo;
    @Autowired private TipoPuertaRepository tipoPuertaRepo;
    @Autowired private EspesorRepository espesorRepo;
    @Autowired private ReglasMarkupRepository reglasMarkupRepo;
    @Autowired private CotizacionDetalleRepository detalleRepo;
    @Autowired private CotizacionRepository cotizacionRepo;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private ColorRepository colorRepo;

     // LISTAR COTIZACIONES (Para la tabla de Angular)

    @Transactional(readOnly = true)
    public Page<CotizacionListadoDTO> listarCotizaciones(String search, String estado, Pageable pageable) {
        Page<Cotizacion> pageEntidades = cotizacionRepo.buscarConFiltros(search, estado, pageable);
        return pageEntidades.map(this::mapToDTO);
    }

    private CotizacionListadoDTO mapToDTO(Cotizacion c) {
        CotizacionListadoDTO dto = new CotizacionListadoDTO();
        dto.setIdCotizacion(c.getIdCotizacion());
        dto.setNumeroCotizacion(c.getNumeroCotizacion());
        dto.setFechaCotizacion(c.getFechaCotizacion());
        dto.setClienteNombre(c.getCliente() != null ? c.getCliente().getRazonSocial() : "Cliente Desconocido");
        dto.setEstado(c.getEstado());
        dto.setMotivo("Cancelada".equalsIgnoreCase(c.getEstado()) ? c.getObservaciones() : null);


        List<CotizacionDetalle> detalles = detalleRepo.findByCotizacion_IdCotizacion(c.getIdCotizacion());

        if (detalles != null && !detalles.isEmpty()) {
            CotizacionDetalle primerDetalle = detalles.get(0);


            if (primerDetalle.getCategoriaProducto() != null) {
                dto.setProductoFamilia(primerDetalle.getCategoriaProducto().getNombre());
            } else {

                dto.setProductoFamilia(primerDetalle.getFamilia_producto() != null ? primerDetalle.getFamilia_producto() : "Sin detalle");
            }

            BigDecimal total = detalles.stream()
                    .map(d -> d.getPrecioVentaSugerido() != null ? d.getPrecioVentaSugerido() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setMontoTotal(total);
        } else {
            dto.setProductoFamilia("Sin detalle");
            dto.setMontoTotal(BigDecimal.ZERO);
        }
        return dto;
    }


     //PREDICCIÓN

    public CotizacionResponseDTO procesarCotizacion(MuebleInputDTO input) {
        CategoriaProducto categoria = categoriaRepo.findById(input.getIdCategoriaProducto())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Base base = baseRepo.findById(input.getIdBase())
                .orElseThrow(() -> new RuntimeException("Base no encontrada"));
        Cerradura cerradura = cerraduraRepo.findById(input.getIdCerradura())
                .orElseThrow(() -> new RuntimeException("Cerradura no encontrada"));
        TipoPuerta tipoPuerta = tipoPuertaRepo.findById(input.getIdTipoPuerta())
                .orElseThrow(() -> new RuntimeException("Tipo de puerta no encontrado"));
        Espesor espesor = espesorRepo.findById(input.getIdEspesor())
                .orElseThrow(() -> new RuntimeException("Espesor no encontrado"));

        PrediccionRequestDTO requestML = PrediccionRequestDTO.builder()
                .familia(categoria.getNombre())
                .alto(input.getAlto())
                .ancho(input.getAncho())
                .fondo(input.getFondo())
                .espesor(espesor.getMilimetros())
                .kgTotal(input.getKgTotal())
                .cerradura(cerradura.getNombre())
                .base(base.getNombre())
                .techoInclinado(input.getTechoInclinado() != null && input.getTechoInclinado() ? 1 : 0)
                .nPuertas(input.getCantidadPuertas())
                .tipoPuerta(tipoPuerta.getNombre())
                .bandejas(input.getCantidadBandejas())
                .divisiones(input.getDivisiones())
                .productoTxt("Locker " + input.getCantidadPuertas() + " puertas")
                .build();

        PrediccionResponseDTO responseML = mlClientService.obtenerPrediccion(requestML);

        Integer cantidadMuebles = (input.getCantidad() != null) ? input.getCantidad() : 1;
        ReglasMarkup regla = reglasMarkupRepo.findReglaByCantidad(cantidadMuebles)
                .orElseThrow(() -> new RuntimeException("No existe regla de markup para la cantidad: " + cantidadMuebles));

        BigDecimal costoFab = responseML.getCostoFabricacionUnitario();
        BigDecimal precioVentaSugerido = costoFab.multiply(regla.getMarkup()).setScale(0, RoundingMode.HALF_UP);

        return CotizacionResponseDTO.builder()
                .costoFabricacionUnitario(costoFab)
                .precioVentaSugerido(precioVentaSugerido)
                .confianzaR2(responseML.getConfianzaR2())
                .modelo(responseML.getModelo())
                .aplicaDescuento(regla.getAplicaDescuento())
                .descuentoSugeridoMin(regla.getDescuentoMinPct())
                .descuentoSugeridoMax(regla.getDescuentoMaxPct())
                .build();
    }

    @Transactional
    public CotizacionResponseDTO procesarYGuardarCotizacion(MuebleInputDTO input, Integer idCliente, Integer idUsuario) {

        CategoriaProducto categoria = categoriaRepo.findById(input.getIdCategoriaProducto()).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Base base = baseRepo.findById(input.getIdBase()).orElseThrow(() -> new RuntimeException("Base no encontrada"));
        Cerradura cerradura = cerraduraRepo.findById(input.getIdCerradura()).orElseThrow(() -> new RuntimeException("Cerradura no encontrada"));
        TipoPuerta tipoPuerta = tipoPuertaRepo.findById(input.getIdTipoPuerta()).orElseThrow(() -> new RuntimeException("Tipo de puerta no encontrado"));
        Espesor espesor = espesorRepo.findById(input.getIdEspesor()).orElseThrow(() -> new RuntimeException("Espesor no encontrado"));

        PrediccionRequestDTO requestML = PrediccionRequestDTO.builder()
                .familia(categoria.getNombre())
                .alto(input.getAlto()).ancho(input.getAncho()).fondo(input.getFondo())
                .espesor(espesor.getMilimetros())
                .kgTotal(input.getKgTotal())
                .cerradura(cerradura.getNombre())
                .base(base.getNombre())
                .techoInclinado(input.getTechoInclinado() != null && input.getTechoInclinado() ? 1 : 0)
                .nPuertas(input.getCantidadPuertas())
                .tipoPuerta(tipoPuerta.getNombre())
                .bandejas(input.getCantidadBandejas())
                .divisiones(input.getDivisiones())
                .productoTxt("Locker " + input.getCantidadPuertas() + " puertas")
                .build();

        PrediccionResponseDTO responseML = mlClientService.obtenerPrediccion(requestML);

        Integer cantidadMuebles = (input.getCantidad() != null) ? input.getCantidad() : 1;
        ReglasMarkup regla = reglasMarkupRepo.findReglaByCantidad(cantidadMuebles)
                .orElseThrow(() -> new RuntimeException("No existe regla de markup para la cantidad: " + cantidadMuebles));

        BigDecimal costoFab = responseML.getCostoFabricacionUnitario();
        BigDecimal precioVentaSugerido = costoFab.multiply(regla.getMarkup()).setScale(0, RoundingMode.HALF_UP);

        Cliente cliente = clienteRepo.findById(idCliente.longValue()).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Usuario usuario = usuarioRepo.findById(idUsuario.longValue()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCliente(cliente);
        cotizacion.setUsuario(usuario);
        cotizacion.setNumeroCotizacion("COT-" + LocalDateTime.now().getYear() + "-" + String.format("%05d", cotizacionRepo.count() + 1));
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
        detalle.setCantidadPuertas(input.getCantidadPuertas());
        detalle.setCantidadBandejas(input.getCantidadBandejas());
        detalle.setTechoInclinado(input.getTechoInclinado() != null && input.getTechoInclinado());
        detalle.setCostoFabricacion(costoFab);
        detalle.setConfianzaMl(responseML.getConfianzaR2());
        detalle.setVersionModeloMl(responseML.getModelo());
        detalle.setMarkupAplicado(regla.getMarkup());
        detalle.setPrecioVentaSugerido(precioVentaSugerido);

        detalle.setAplicaDescuento(regla.getAplicaDescuento() != null ? regla.getAplicaDescuento() : false);
        detalle.setDescuentoSugeridoMin(regla.getDescuentoMinPct());
        detalle.setDescuentoSugeridoMax(regla.getDescuentoMaxPct());

        detalle.setIdColor(input.getIdColor() != null ? input.getIdColor() : 1);
        detalle.setIdEspesor(input.getIdEspesor());

        detalleRepo.save(detalle);

        return CotizacionResponseDTO.builder()
                .costoFabricacionUnitario(costoFab)
                .precioVentaSugerido(precioVentaSugerido)
                .confianzaR2(responseML.getConfianzaR2())
                .modelo(responseML.getModelo())
                .aplicaDescuento(regla.getAplicaDescuento())
                .descuentoSugeridoMin(regla.getDescuentoMinPct())
                .descuentoSugeridoMax(regla.getDescuentoMaxPct())
                .build();
    }

    @Transactional
    public void cancelarCotizacion(Integer id, String motivo) {
        Cotizacion cotizacion = cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        cotizacion.setEstado("CANCELADA");
        cotizacion.setObservaciones(motivo);

        cotizacionRepo.save(cotizacion);
    }

    public CotizacionListadoDTO obtenerCotizacionPorId(Integer id) {
        Cotizacion c = cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        return mapToDTO(c);
    }

    @Transactional
    public CotizacionResponseDTO actualizarCotizacion(Integer idCotizacion, MuebleInputDTO input) {
        Cotizacion cotizacion = cotizacionRepo.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        List<CotizacionDetalle> detalles = detalleRepo.findByCotizacion_IdCotizacion(idCotizacion);
        if (detalles.isEmpty()) {
            throw new RuntimeException("La cotización no tiene detalles para actualizar");
        }
        CotizacionDetalle detalle = detalles.get(0);

        CategoriaProducto categoria = categoriaRepo.findById(input.getIdCategoriaProducto())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Base base = baseRepo.findById(input.getIdBase())
                .orElseThrow(() -> new RuntimeException("Base no encontrada"));
        Cerradura cerradura = cerraduraRepo.findById(input.getIdCerradura())
                .orElseThrow(() -> new RuntimeException("Cerradura no encontrada"));
        TipoPuerta tipoPuerta = tipoPuertaRepo.findById(input.getIdTipoPuerta())
                .orElseThrow(() -> new RuntimeException("Tipo de puerta no encontrado"));
        Espesor espesor = espesorRepo.findById(input.getIdEspesor())
                .orElseThrow(() -> new RuntimeException("Espesor no encontrado"));

        PrediccionRequestDTO requestML = PrediccionRequestDTO.builder()
                .familia(categoria.getNombre())
                .alto(input.getAlto())
                .ancho(input.getAncho())
                .fondo(input.getFondo())
                .espesor(espesor.getMilimetros())
                .kgTotal(input.getKgTotal())
                .cerradura(cerradura.getNombre())
                .base(base.getNombre())
                .techoInclinado(input.getTechoInclinado() != null && input.getTechoInclinado() ? 1 : 0)
                .nPuertas(input.getCantidadPuertas())
                .tipoPuerta(tipoPuerta.getNombre())
                .bandejas(input.getCantidadBandejas())
                .divisiones(input.getDivisiones())
                .productoTxt("Locker " + input.getCantidadPuertas() + " puertas")
                .build();

        PrediccionResponseDTO responseML = mlClientService.obtenerPrediccion(requestML);

        Integer cantidadMuebles = (input.getCantidad() != null) ? input.getCantidad() : 1;
        ReglasMarkup regla = reglasMarkupRepo.findReglaByCantidad(cantidadMuebles)
                .orElseThrow(() -> new RuntimeException("No existe regla de markup para la cantidad: " + cantidadMuebles));

        BigDecimal costoFab = responseML.getCostoFabricacionUnitario();
        BigDecimal precioVentaSugerido = costoFab.multiply(regla.getMarkup()).setScale(0, RoundingMode.HALF_UP);

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
        detalle.setCantidadPuertas(input.getCantidadPuertas());
        detalle.setCantidadBandejas(input.getCantidadBandejas());
        detalle.setTechoInclinado(input.getTechoInclinado() != null && input.getTechoInclinado());

        detalle.setCostoFabricacion(costoFab);
        detalle.setConfianzaMl(responseML.getConfianzaR2());
        detalle.setVersionModeloMl(responseML.getModelo());
        detalle.setMarkupAplicado(regla.getMarkup());
        detalle.setPrecioVentaSugerido(precioVentaSugerido);

        detalle.setAplicaDescuento(regla.getAplicaDescuento() != null ? regla.getAplicaDescuento() : false);
        detalle.setDescuentoSugeridoMin(regla.getDescuentoMinPct());
        detalle.setDescuentoSugeridoMax(regla.getDescuentoMaxPct());


        detalleRepo.save(detalle);

        return CotizacionResponseDTO.builder()
                .costoFabricacionUnitario(costoFab)
                .precioVentaSugerido(precioVentaSugerido)
                .confianzaR2(responseML.getConfianzaR2())
                .modelo(responseML.getModelo())
                .aplicaDescuento(regla.getAplicaDescuento())
                .descuentoSugeridoMin(regla.getDescuentoMinPct())
                .descuentoSugeridoMax(regla.getDescuentoMaxPct())
                .build();
    }

    public CotizacionDetalleDTO obtenerCotizacionDetalle(Integer id) {
        Cotizacion c = cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        List<CotizacionDetalle> detalles = detalleRepo.findByCotizacion_IdCotizacion(id);
        if (detalles.isEmpty()) throw new RuntimeException("La cotización no tiene detalles");

        CotizacionDetalle d = detalles.get(0);

        String espesorStr = "N/A";
        if (d.getIdEspesor() != null) {
            Espesor esp = espesorRepo.findById(d.getIdEspesor()).orElse(null);
            if (esp != null) espesorStr = esp.getMilimetros() + " mm";
        }

        String colorStr = "N/A";
        if (d.getIdColor() != null) {
            Color col = colorRepo.findById(d.getIdColor()).orElse(null);
            if (col != null) colorStr = col.getNombre();
        }

        String tipoPuertaStr = "N/A";
        if (d.getIdTipoPuerta() != null) {
            TipoPuerta tp = tipoPuertaRepo.findById(d.getIdTipoPuerta()).orElse(null);
            if (tp != null) tipoPuertaStr = tp.getNombre();
        }


        return CotizacionDetalleDTO.builder()
                .idCotizacion(c.getIdCotizacion())
                .numeroCotizacion(c.getNumeroCotizacion())
                .fechaCotizacion(c.getFechaCotizacion())
                .estado(c.getEstado())
                .observaciones(c.getObservaciones())
                .clienteRazonSocial(c.getCliente() != null ? c.getCliente().getRazonSocial() : "N/A")
                .clienteRut(c.getCliente() != null ? c.getCliente().getRut() : "N/A")
                .clienteCorreo(c.getCliente() != null ? c.getCliente().getCorreo() : "N/A")
                .clienteTelefono(c.getCliente() != null ? c.getCliente().getTelefono() : "N/A")
                .categoria(d.getCategoriaProducto() != null ? d.getCategoriaProducto().getNombre() : "N/A")
                .cantidad(d.getCantidad())
                .altoCm(d.getAltoCm())
                .anchoCm(d.getAnchoCm())
                .fondoCm(d.getFondoCm())
                .espesor(espesorStr)
                .color(colorStr)
                .cerradura(d.getCerradura() != null ? d.getCerradura().getNombre() : "N/A")
                .base(d.getBase() != null ? d.getBase().getNombre() : "N/A")
                .tipoPuerta(tipoPuertaStr)
                .cantidadPuertas(d.getCantidadPuertas())
                .cantidadBandejas(d.getCantidadBandejas())
                .techoInclinado(d.getTechoInclinado())
                .costoFabricacion(d.getCostoFabricacion())
                .markupAplicado(d.getMarkupAplicado())
                .confianzaMl(d.getConfianzaMl())
                .precioVentaSugerido(d.getPrecioVentaSugerido())
                .build();
    }

    @Transactional
    public void aceptarCotizacion(Integer id) {
        Cotizacion cotizacion = cotizacionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        cotizacion.setEstado("ACEPTADA");
        cotizacion.setObservaciones("Aceptada por el asesor comercial.");

        cotizacionRepo.save(cotizacion);
    }
}