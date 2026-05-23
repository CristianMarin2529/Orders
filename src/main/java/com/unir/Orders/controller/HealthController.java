package com.unir.Orders.controller;

import com.unir.Orders.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Health Check Controller - Verifica que el servidor esté arriba
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    // GET /api/v1/health
    @GetMapping
    public String health() {
        return healthService.getStatus();
    }
}
