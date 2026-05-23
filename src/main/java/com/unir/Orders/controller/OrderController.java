package com.unir.Orders.controller;

import com.unir.Orders.controller.dto.CreateOrderRequest;
import com.unir.Orders.controller.dto.OrderResponse;
import com.unir.Orders.controller.dto.OrderItemResponse;
import com.unir.Orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CONTROLLER - Mapea peticiones HTTP a métodos del Service
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // GET /api/v1/orders/users/{userId}
    @GetMapping("/users/{userId}")
    public List<OrderResponse> getOrdersByUserId(@PathVariable String userId) {
        return orderService.getOrdersByUserId(userId);
    }

    // POST /api/v1/orders
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    // GET /api/v1/orders/{orderId}/items - Obtener todos los detalles de una orden
    @GetMapping("/{orderId}/items")
    public List<OrderItemResponse> getOrderItems(@PathVariable String orderId) {
        return orderService.getOrderItems(orderId);
    }

    // GET /api/v1/orders/{orderId}/items/{itemId} - Obtener un item específico de una orden
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemResponse getOrderItem(@PathVariable String orderId, @PathVariable Long itemId) {
        return orderService.getOrderItem(orderId, itemId);
    }
}
