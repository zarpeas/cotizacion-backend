package com.cotizacion.cotizacion_backend.service;

import com.cotizacion.cotizacion_backend.dto.PrediccionRequestDTO;
import com.cotizacion.cotizacion_backend.dto.PrediccionResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MLClientService {

    @Value("${ml.api.url:http://127.0.0.1:8000/predecir}")
    private String mlApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public PrediccionResponseDTO obtenerPrediccion(PrediccionRequestDTO request) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<PrediccionRequestDTO> entity = new HttpEntity<>(request, headers);

            ResponseEntity<PrediccionResponseDTO> response = restTemplate.postForEntity(
                    mlApiUrl,
                    entity,
                    PrediccionResponseDTO.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con la API de Machine Learning: " + e.getMessage());
        }
    }
}