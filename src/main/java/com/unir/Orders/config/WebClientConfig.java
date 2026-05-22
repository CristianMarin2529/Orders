package com.unir.Orders.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Instancia WebClient para llamadas HTTP reactivas
 */
@Configuration
public class WebClientConfig {

    @Value("${catalogue.base-url:http://localhost:8085}")
    private String catalogueBaseUrl;

    /**
     * Bean de WebClient apuntando al servicio Catalogue
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(catalogueBaseUrl)
                .build();
    }
}

