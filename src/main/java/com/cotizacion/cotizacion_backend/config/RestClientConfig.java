package com.cotizacion.cotizacion_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    @Bean
    public RestClient mlRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(mlServiceUrl)
                .build();
    }
}