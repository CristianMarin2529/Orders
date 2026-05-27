package com.unir.Orders.service;

import com.unir.Orders.client.CatalogueClient;
import com.unir.Orders.client.dto.CatalogueBookResponse;
import com.unir.Orders.client.dto.StockAdjustmentRequestDto;
import com.unir.Orders.controller.dto.CreateOrderRequest;
import com.unir.Orders.controller.dto.OrderResponse;
import com.unir.Orders.controller.dto.OrderItemResponse;
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
import java.util.UUID;

/**
 * SERVICE - Contiene la lógica de negocio de orders
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

            // Llamada a microservicio Catalogue

            CatalogueBookResponse book = catalogueClient.getBookById(item.getBookId().intValue())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado en catálogo"));

            // Valida que el libro esté activo/disponible
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

            validatedBooks.add(book);
        }

        Order order = new Order(
                generateOrderId(),
                request.getUserId(),
                LocalDate.now(),
                total,
                "Procesando"
        );

        Order savedOrder = orderRepository.save(order);

        List<CreateOrderRequest.Item> requestItems = request.getItems();
        for (int i = 0; i < requestItems.size(); i++) {
            CreateOrderRequest.Item requestItem = requestItems.get(i);
            CatalogueBookResponse book = validatedBooks.get(i);

            // Calcular subtotal nuevamente para este item
            BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(requestItem.getQuantity()));

            // Crear y guardar OrderItem
            OrderItem orderItem = new OrderItem(
                    null,
                    savedOrder.getId(),
                    book.getId(),
                    book.getTitle(),
                    book.getDescription() != null ? book.getDescription() : "N/A",
                    requestItem.getQuantity(),
                    book.getPrice(),
                    subtotal,
                    LocalDateTime.now()
            );

            orderItemRepository.save(orderItem);

            log.info("Item guardado: Orden {} - Libro: {} - Cantidad: {}",
                    savedOrder.getId(), book.getTitle(), requestItem.getQuantity());
        }

        // Descontar stock en Catalogue
        try {
            StockAdjustmentRequestDto stockRequest = new StockAdjustmentRequestDto();
            List<StockAdjustmentRequestDto.BookQuantity> bookQuantities = new ArrayList<>();

            for (CreateOrderRequest.Item item : requestItems) {
                bookQuantities.add(new StockAdjustmentRequestDto.BookQuantity(item.getBookId().intValue(), item.getQuantity()));
            }

            stockRequest.setBooks(bookQuantities);
            catalogueClient.decreaseStock(stockRequest);
            log.info("Stock descontado en Catalogue para orden: {}", savedOrder.getId());

        } catch (Exception ex) {
            log.error("Error descontando stock en Catalogue para orden {}: {}", savedOrder.getId(), ex.getMessage());
            // Rollback: eliminar la orden si falla el descuento de stock
            orderRepository.deleteById(savedOrder.getId());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo procesar el descuento de stock. Orden cancelada.");
        }

        return toResponse(savedOrder);
    }

    private OrderResponse toResponse(Order order) {

        String fechaFormateada = order.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                fechaFormateada,
                order.getTotal(),
                order.getEstado()
        );
    }

    // Genera ID único para orden Formato: PED-YYYY-XXX con consecutivo

    private String generateOrderId() {
        // Genera ID único usando UUID (8 caracteres hexadecimales)
        // El timestamp se registra automáticamente en la columna fecha_creacion
        // Formato: PED-2026-A1B2C3D4
        String uuidPart = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();

        int year = LocalDate.now().getYear();
        return String.format("PED-%d-%s", year, uuidPart);
    }

    // Obtiene todos los items (detalles) de una orden específica

    public List<OrderItemResponse> getOrderItems(String orderId) {
        log.info("Obteniendo items para orden: {}", orderId);

        // Verificar que la orden existe
        orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));

        List<OrderItemResponse> items = orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::toItemResponse)
                .toList();

        log.debug("Se encontraron {} items para orden {}", items.size(), orderId);
        return items;
    }

    //Obtiene un item específico de una orden

    public OrderItemResponse getOrderItem(String orderId, Long itemId) {
        log.info("Obteniendo item {} de orden: {}", itemId, orderId);

        // Verificar que la orden existe
        orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado"));

        // Validar que el item pertenece a la orden
        if (!item.getOrderId().equals(orderId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El item no pertenece a esta orden");
        }

        return toItemResponse(item);
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getOrderId(),
                item.getBookId(),
                item.getBookTitle(),
                item.getBookAuthor(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal(),
                item.getFechaCreacion()
        );
    }
}
