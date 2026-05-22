package com.unir.Orders.client;

import com.unir.Orders.client.dto.CatalogueBookResponse;

import java.util.Optional;

public interface CatalogueClient {

    /**
     * Obtiene un libro del microservicio Catalogue
     */
    Optional<CatalogueBookResponse> getBookById(Integer bookId);
}