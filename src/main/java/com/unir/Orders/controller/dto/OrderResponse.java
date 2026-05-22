package com.unir.Orders.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) - Response
 * Lo que se ENVÍA al cliente en formato JSON.
 *
 * Lombok genera automáticamente getters, setters, constructores, equals, hashCode y toString
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("fecha")
    private String fecha;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("estado")
    private String estado;
}

