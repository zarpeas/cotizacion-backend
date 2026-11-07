package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.PrediccionRequestDTO;
import com.cotizacion.cotizacion_backend.dto.PrediccionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MLClientService {

    @Autowired
    private RestClient restClient;

    @Value("${ml.api.url:http://127.0.0.1:8000/predecir}")
    private String mlApiUrl;

    public PrediccionResponseDTO obtenerPrediccion(PrediccionRequestDTO request) {
        try {
            return restClient.post()
                    .uri(mlApiUrl)
                    .body(request)
                    .retrieve()
                    .body(PrediccionResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con la API de Machine Learning: " + e.getMessage());
        }
    }
}