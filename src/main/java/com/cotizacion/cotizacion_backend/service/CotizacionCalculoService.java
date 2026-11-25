package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.*;
import com.cotizacion.cotizacion_backend.entity.*;
import com.cotizacion.cotizacion_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Resuelve catálogos, arma el request al ML y aplica la regla de markup. */
@Service
public class CotizacionCalculoService {

    @Autowired private MLClientService mlClientService;
    @Autowired private BaseRepository baseRepo;
    @Autowired private CerraduraRepository cerraduraRepo;
    @Autowired private CategoriaProductoRepository categoriaRepo;
    @Autowired private TipoPuertaRepository tipoPuertaRepo;
    @Autowired private EspesorRepository espesorRepo;
    @Autowired private ReglasMarkupRepository reglasMarkupRepo;

    /** Resultado reutilizable por el guardado y la actualización. */
    public record Resultado(
            CategoriaProducto categoria,
            Base base,
            Cerradura cerradura,
            TipoPuerta tipoPuerta,
            Espesor espesor,
            ReglasMarkup regla,
            PrediccionResponseDTO prediccion,
            Integer nPuertas,
            Integer nCuerpos,
            BigDecimal costoFabricacion,
            BigDecimal precioVentaSugerido) {}

    /**
     * @param forzarSinPuerta reproducing el comportamiento del endpoint /predecir,
     *                        donde el tipo de puerta 4 se trata siempre como sin puerta.
     */
    public Resultado calcular(MuebleInputDTO input, boolean forzarSinPuerta) {
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

        Integer nPuertas = (input.getCantidadPuertas() != null) ? input.getCantidadPuertas() : 0;
        Integer nCuerpos = (input.getCantidadCasilleros() != null && input.getCantidadCasilleros() > 0)
                ? input.getCantidadCasilleros()
                : 1;

        if (forzarSinPuerta && input.getIdTipoPuerta() != null && input.getIdTipoPuerta() == 4) {
            nPuertas = 0;
            if (nCuerpos == null || nCuerpos == 0) nCuerpos = 1;
        }

        String productoTxt = "Locker " + nPuertas + " puertas";
        if (nPuertas == 0) {
            productoTxt = "Locker " + nCuerpos + " casilleros (sin puerta)";
        } else if (nCuerpos > 1) {
            productoTxt = "Locker " + nCuerpos + " cuerpos " + nPuertas + " puertas";
        }

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
                .nPuertas(nPuertas != null ? nPuertas : 0)
                .nCuerpos(nCuerpos != null ? nCuerpos : 1)
                .tipoPuerta(tipoPuerta.getNombre())
                .bandejas(input.getCantidadBandejas())
                .divisiones(input.getDivisiones())
                .productoTxt(productoTxt)
                .build();

        PrediccionResponseDTO prediccion = mlClientService.obtenerPrediccion(requestML);

        Integer cantidadMuebles = (input.getCantidad() != null) ? input.getCantidad() : 1;
        ReglasMarkup regla = reglasMarkupRepo.findReglaByCantidad(cantidadMuebles)
                .orElseThrow(() -> new RuntimeException("No existe regla de markup para la cantidad: " + cantidadMuebles));

        BigDecimal costoCrudo = prediccion.getCostoFabricacionUnitario();
        // El peso chileno no tiene centavos: se guarda en pesos enteros.
        BigDecimal costoFabricacion = costoCrudo.setScale(0, RoundingMode.HALF_UP);
        // El precio se calcula con el valor crudo y se redondea una sola vez.
        BigDecimal precioVentaSugerido = costoCrudo.multiply(regla.getMarkup())
                .setScale(0, RoundingMode.HALF_UP);

        return new Resultado(
                categoria, base, cerradura, tipoPuerta, espesor, regla, prediccion,
                nPuertas, nCuerpos, costoFabricacion, precioVentaSugerido);
    }

    /** Respuesta común a predicción, guardado y actualización. */
    public CotizacionResponseDTO respuesta(Resultado resultado) {
        PrediccionResponseDTO prediccion = resultado.prediccion();
        ReglasMarkup regla = resultado.regla();

        return CotizacionResponseDTO.builder()
                .costoFabricacionUnitario(resultado.costoFabricacion())
                .precioVentaSugerido(resultado.precioVentaSugerido())
                .confianzaR2(prediccion.getConfianzaR2())
                .mapeEsperadoPct(prediccion.getMapeEsperadoPct())
                .mapeMedianaPct(prediccion.getMapeMedianaPct())
                .modelo(prediccion.getModelo())
                .aplicaDescuento(regla.getAplicaDescuento())
                .descuentoSugeridoMin(regla.getDescuentoMinPct())
                .descuentoSugeridoMax(regla.getDescuentoMaxPct())
                .build();
    }
}
