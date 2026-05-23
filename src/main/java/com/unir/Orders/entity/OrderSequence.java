package com.unir.Orders.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Secuencia persistente para generar IDs de órdenes consecutivos.

@Entity
@Table(name = "order_sequence")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

