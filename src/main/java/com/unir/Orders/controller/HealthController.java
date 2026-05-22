package com.unir.Orders.controller;

import com.unir.Orders.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller - Verifica que el servidor esté arriba
 */
@RestController
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping("/")
    public String home() {
        return healthService.getStatus();
    }
}
