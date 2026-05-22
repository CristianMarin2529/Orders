package com.unir.Orders.controller.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO (Data Transfer Object) - Request
 * Lo que RECIBE el servidor del cliente en formato JSON.
 * Lombok genera automáticamente getters, setters, constructor, equals, hashCode y toString
 */
@Data
public class CreateOrderRequest {
    private String userId;
    private List<Item> items;

    @Data
    public static class Item {
        private Long bookId;
        private Integer quantity;
    }
}

