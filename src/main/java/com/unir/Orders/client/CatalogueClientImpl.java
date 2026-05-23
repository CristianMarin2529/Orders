package com.unir.Orders.client;

import com.unir.Orders.client.dto.CatalogueBookResponse;
import com.unir.Orders.client.dto.StockAdjustmentRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogueClientImpl implements CatalogueClient {

    private final WebClient webClient;

    // GET /api/v1/internal/books/{id}/detail

    @Override
    public Optional<CatalogueBookResponse> getBookById(Integer bookId) {
        try {
            CatalogueBookResponse response = webClient
                    .get()
                    .uri("/api/v1/internal/books/{id}/detail", bookId)
                    .retrieve()
                    .bodyToMono(CatalogueBookResponse.class)
                    .block();

            return Optional.ofNullable(response);

        } catch (WebClientResponseException.NotFound ex) {
            log.warn("Libro no encontrado en Catalogue: {}", bookId);
            return Optional.empty();

        } catch (Exception ex) {
            log.error("Error consultando Catalogue (/api/v1/internal/books/{}/detail): {}", bookId, ex.getMessage());
            return Optional.empty();
        }
    }

    // POST /api/v1/internal/books/stock/decrease
    // Descuenta stock de múltiples libros en Catalogue

    @Override
    public void decreaseStock(StockAdjustmentRequestDto request) {
        try {
            webClient
                    .post()
                    .uri("/api/v1/internal/books/stock/decrease")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Stock descontado en Catalogue para {} libros", request.getBooks().size());

        } catch (Exception ex) {
            log.error("Error descontando stock en Catalogue: {}", ex.getMessage());
            throw new RuntimeException("No se pudo descontar stock: " + ex.getMessage(), ex);
        }
    }

    // POST /api/v1/internal/books/stock/increase
    // Aumenta stock de múltiples libros en Catalogue (devoluciones)

    @Override
    public void increaseStock(StockAdjustmentRequestDto request) {
        try {
            webClient
                    .post()
                    .uri("/api/v1/internal/books/stock/increase")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Stock aumentado en Catalogue para {} libros", request.getBooks().size());

        } catch (Exception ex) {
            log.error("Error aumentando stock en Catalogue: {}", ex.getMessage());
            throw new RuntimeException("No se pudo aumentar stock: " + ex.getMessage(), ex);
        }
    }
}



