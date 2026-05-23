package com.unir.Orders.client;

import com.unir.Orders.client.dto.CatalogueBookResponse;
import com.unir.Orders.client.dto.StockAdjustmentRequestDto;

import java.util.Optional;

public interface CatalogueClient {

    /**
     * Obtiene un libro del microservicio Catalogue
     */
    Optional<CatalogueBookResponse> getBookById(Integer bookId);

    // Descuenta stock de libros en Catalogue POST /api/v1/internal/books/stock/decrease
    void decreaseStock(StockAdjustmentRequestDto request);

    // Aumenta stock de libros en Catalogue (para devoluciones) POST /api/v1/internal/books/stock/increase

    void increaseStock(StockAdjustmentRequestDto request);
}