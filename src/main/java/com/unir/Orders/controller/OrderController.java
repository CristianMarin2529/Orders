package com.unir.Orders.controller;

import com.unir.Orders.controller.dto.CreateOrderRequest;
import com.unir.Orders.controller.dto.OrderResponse;
import com.unir.Orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER - Mapea peticiones HTTP a métodos del Service
 *
 * @RestController: genera JSON automáticamente en respuestas.
 * Responsabilidad: recibir request, llamar Service, devolver response.
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * GET /users/{userId}/orders
     * Obtiene las órdenes de un usuario
     */
    @GetMapping("/users/{userId}/orders")
    public List<OrderResponse> getOrdersByUserId(@PathVariable String userId) {
        return orderService.getOrdersByUserId(userId);
    }

    // POST- Crea nuevas ordenes con validación de Stock
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}
