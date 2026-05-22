package com.unir.Orders.controller;

import com.unir.Orders.client.CatalogueClient;
import com.unir.Orders.client.dto.CatalogueBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/proxy-catalogue")
@RequiredArgsConstructor
public class CatalogueProxyController {

    private final CatalogueClient catalogueClient;

    /**
     * GET /proxy-catalogue/books/{bookId}
     * Endpoint de debug para verificar que se puede obtener un libro de Catalogue
     */
    @GetMapping("/books/{bookId}")
    public Optional<CatalogueBookResponse> getBookById(@PathVariable Integer bookId) {
        return catalogueClient.getBookById(bookId);
    }
}
