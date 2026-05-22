package com.unir.Orders.repository;

import com.unir.Orders.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Obtiene los detalles de una orden
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {


    List<OrderItem> findByOrderId(String orderId);
    List<OrderItem> findByOrderIdAndBookId(String orderId, Integer bookId);
}

