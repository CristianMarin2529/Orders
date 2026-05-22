package com.unir.Orders.service;

import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public String getStatus() {
        return "Orders backend running";
    }
}