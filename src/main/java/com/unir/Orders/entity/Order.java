package com.unir.Orders.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ENTITY - Representación de la tabla "orders" en la base de datos
 *
 * Esta clase mapea directamente a la tabla MySQL "orders" y representa
 * una orden/compra de un usuario.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "estado")
    private String estado;
}
