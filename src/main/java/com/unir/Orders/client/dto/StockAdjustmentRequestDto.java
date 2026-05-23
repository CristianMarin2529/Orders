package com.unir.Orders.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Solicitar ajuste de stock a Catalogue usado POST /api/v1/internal/books/stock/decrease
 * y POST /api/v1/internal/books/stock/increase
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequestDto {

    @JsonProperty("books")
    private List<BookQuantity> books;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookQuantity {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("quantity")
        private Integer quantity;
    }
}

