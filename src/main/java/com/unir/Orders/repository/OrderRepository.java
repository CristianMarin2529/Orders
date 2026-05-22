package com.unir.Orders.repository;

import com.unir.Orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY - Patrón DAO (Data Access Object)
 *
 * Proporciona operaciones CRUD (Create, Read, Update, Delete) sobre la entidad Order.
 * Spring Data JPA genera automáticamente la implementación de los métodos heredados de JpaRepository.
 *
 * Flujo:
 * 1. El Service llama al Repository
 * 2. El Repository ejecuta queries contra la BD
 * 3. La BD retorna datos
 * 4. JpaRepository mapea los resultados a entidades Order
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    // SQL generado: SELECT * FROM orders WHERE user_id = ? ORDER BY fecha DESC
    List<Order> findByUserIdOrderByFechaDesc(String userId);
}

