package com.unir.Orders.service;

import com.unir.Orders.client.CatalogueClient;
import com.unir.Orders.client.dto.CatalogueBookResponse;
import com.unir.Orders.controller.dto.CreateOrderRequest;
import com.unir.Orders.controller.dto.OrderResponse;
import com.unir.Orders.entity.Order;
import com.unir.Orders.entity.OrderItem;
import com.unir.Orders.repository.OrderRepository;
import com.unir.Orders.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SERVICE - Contiene la lógica de negocio de órdenes
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CatalogueClient catalogueClient;


    /**
     * Obtiene órdenes de un usuario (BD - Entity - DTO)
     */
    public List<OrderResponse> getOrdersByUserId(String userId) {
        log.info("Obteniendo órdenes para usuario: {}", userId);
        List<OrderResponse> orders = orderRepository.findByUserIdOrderByFechaDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
        log.debug("Se encontraron {} órdenes", orders.size());
        return orders;
    }

    // Crea una nueva orden validando stock y disponibilidad
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Validación 1: userId no vacío
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El userId es obligatorio");
        }

        // Validación 2: items no vacío
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La compra debe tener al menos un item");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<CatalogueBookResponse> validatedBooks = new ArrayList<>();

        // Validar cada item y calcular total
        for (CreateOrderRequest.Item item : request.getItems()) {
            if (item.getBookId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El bookId es obligatorio");
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero");
            }

            // Llamada a microservicio Catalogue (Real): GET /books/{bookId}
            // BookId puede ser Long o Integer, se convierte automáticamente
            CatalogueBookResponse book = catalogueClient.getBookById(item.getBookId().intValue())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado en catálogo"));

            // Validar que el libro esté activo/disponible
            if (!Boolean.TRUE.equals(book.getIsActive())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El libro no está disponible");
            }

            // Validar stock suficiente
            if (book.getStock() == null || book.getStock() < item.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para " + book.getTitle());
            }

            // Calcular subtotal: price * quantity
            BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);

            // NUEVO: Guardar la información del libro validado para después
            validatedBooks.add(book);
        }

        // Crear entity con los datos validados
        Order order = new Order(
                generateOrderId(),
                request.getUserId(),
                LocalDate.now(), // Fecha actual del servidor
                total,
                "Procesando" // Estado inicial
        );

        // Guardar en BD y retornar como DTO
        Order savedOrder = orderRepository.save(order);

        // NUEVO: Guardar detalles de cada item en order_items
        List<CreateOrderRequest.Item> requestItems = request.getItems();
        for (int i = 0; i < requestItems.size(); i++) {
            CreateOrderRequest.Item requestItem = requestItems.get(i);
            CatalogueBookResponse book = validatedBooks.get(i);

            // Calcular subtotal nuevamente para este item
            BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(requestItem.getQuantity()));

            // Crear y guardar OrderItem
            OrderItem orderItem = new OrderItem(
                    null,  // id será autogenerado por BD
                    savedOrder.getId(),
                    book.getId(),
                    book.getTitle(),
                    book.getDescription() != null ? book.getDescription() : "N/A",
                    requestItem.getQuantity(),
                    book.getPrice(),
                    subtotal,
                    LocalDateTime.now()  // fechaCreacion
            );

            orderItemRepository.save(orderItem);

            System.out.println("✓ Item guardado: Orden " + savedOrder.getId() +
                    " - Libro: " + book.getTitle() +
                    " - Cantidad: " + requestItem.getQuantity());
        }

        return toResponse(savedOrder);
    }

    private OrderResponse toResponse(Order order) {
        // Formatear fecha: LocalDate -> String "dd/MM/yyyy"
        String fechaFormateada = order.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                fechaFormateada,
                order.getTotal(),
                order.getEstado()
        );
    }

    // Genera ID único para orden Formato: PED-2026-XXX donde XXX es random 100-999

    private String generateOrderId() {
        return "PED-2026-" + ThreadLocalRandom.current().nextInt(100, 1000);
    }
}
