package com.unir.Orders.client;

import com.unir.Orders.client.dto.CatalogueBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class CatalogueClientImpl implements CatalogueClient {

    private final WebClient webClient;

    /**
     * GET /books/{id} → Catalogue
     * Mapea automáticamente BookResponseDto de Catalogue → CatalogueBookResponse de Orders
     */
    @Override
    public Optional<CatalogueBookResponse> getBookById(Integer bookId) {
        try {
            CatalogueBookResponse response = webClient
                    .get()
                    .uri("/books/{id}", bookId)
                    .retrieve()
                    .bodyToMono(CatalogueBookResponse.class)
                    .block(); // Bloqueante: espera respuesta HTTP

            return Optional.ofNullable(response);

        } catch (WebClientResponseException.NotFound ex) {
            // 404: Libro no existe en catálogo
            return Optional.empty();

        } catch (Exception ex) {
            // Timeout, conexión rechazada, etc.
            System.err.println("Error consultando Catalogue (/books/" + bookId + "): " + ex.getMessage());
            return Optional.empty();
        }
    }
}


