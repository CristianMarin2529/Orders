package com.unir.Orders.repository;

import com.unir.Orders.entity.OrderSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSequenceRepository extends JpaRepository<OrderSequence, Long> {
}

