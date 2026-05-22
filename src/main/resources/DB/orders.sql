CREATE DATABASE IF NOT EXISTS orders;

USE orders;

-- Crear tabla de órdenes
CREATE TABLE IF NOT EXISTS orders (
                                      id VARCHAR(20) PRIMARY KEY,
                                      user_id VARCHAR(20) NOT NULL,
                                      fecha DATE NOT NULL,
                                      total DECIMAL(12, 2) NOT NULL,
                                      estado VARCHAR(50) NOT NULL,
                                      fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      INDEX idx_orders_user_fecha (user_id, fecha)
);

-- Crear tabla de detalle de ordenes
-- Esta tabla guarda una foto de los libros comprados en el momento de la compra.
-- No reemplaza al microservicio catalogue: solo conserva el acuse historico de la orden.
CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           order_id VARCHAR(20) NOT NULL,
                                           book_id BIGINT NOT NULL,
                                           book_title VARCHAR(255) NOT NULL,
                                           book_author VARCHAR(255),
                                           quantity INT NOT NULL,
                                           unit_price DECIMAL(12, 2) NOT NULL,
                                           subtotal DECIMAL(12, 2) NOT NULL,
                                           fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           CONSTRAINT fk_order_items_order
                                               FOREIGN KEY (order_id) REFERENCES orders(id)
                                                   ON DELETE CASCADE,
                                           CONSTRAINT uk_order_items_order_book
                                               UNIQUE (order_id, book_id),
                                           CONSTRAINT chk_order_items_quantity
                                               CHECK (quantity > 0),
                                           CONSTRAINT chk_order_items_unit_price
                                               CHECK (unit_price >= 0),
                                           CONSTRAINT chk_order_items_subtotal
                                               CHECK (subtotal >= 0),
                                           INDEX idx_order_items_order_id (order_id),
                                           INDEX idx_order_items_book_id (book_id)
);

-- Insertar 20 registros de ejemplo
INSERT IGNORE INTO orders (id, user_id, fecha, total, estado) VALUES
                                                                  ('PED-2026-001', 'user-1', '2026-01-05', 60000.00, 'Entregado'),
                                                                  ('PED-2026-002', 'user-2', '2026-01-08', 45000.00, 'Procesando'),
                                                                  ('PED-2026-003', 'user-3', '2026-01-12', 75000.00, 'Cancelado'),
                                                                  ('PED-2026-004', 'user-4', '2026-01-15', 32000.00, 'Entregado'),
                                                                  ('PED-2026-005', 'user-5', '2026-01-18', 88000.00, 'En Tránsito'),
                                                                  ('PED-2026-006', 'user-6', '2026-02-02', 52000.00, 'Entregado'),
                                                                  ('PED-2026-007', 'user-7', '2026-02-05', 65000.00, 'Procesando'),
                                                                  ('PED-2026-008', 'user-8', '2026-02-10', 41000.00, 'Entregado'),
                                                                  ('PED-2026-009', 'user-9', '2026-02-14', 95000.00, 'En Tránsito'),
                                                                  ('PED-2026-010', 'user-10', '2026-02-20', 28000.00, 'Procesando'),
                                                                  ('PED-2026-011', 'user-11', '2026-03-05', 72000.00, 'Entregado'),
                                                                  ('PED-2026-012', 'user-12', '2026-03-10', 58000.00, 'Cancelado'),
                                                                  ('PED-2026-013', 'user-13', '2026-03-15', 46000.00, 'En Tránsito'),
                                                                  ('PED-2026-014', 'user-14', '2026-03-20', 81000.00, 'Entregado'),
                                                                  ('PED-2026-015', 'user-15', '2026-03-25', 39000.00, 'Procesando'),
                                                                  ('PED-2026-016', 'user-16', '2026-04-02', 67000.00, 'Entregado'),
                                                                  ('PED-2026-017', 'user-17', '2026-04-08', 54000.00, 'En Tránsito'),
                                                                  ('PED-2026-018', 'user-18', '2026-04-15', 71000.00, 'Entregado'),
                                                                  ('PED-2026-019', 'user-19', '2026-04-22', 33000.00, 'Procesando'),
                                                                  ('PED-2026-108', 'user-1', '2026-04-28', 60000.00, 'Entregado');

-- Insertar detalles de ejemplo para algunas ordenes historicas
INSERT IGNORE INTO order_items (order_id, book_id, book_title, book_author, quantity, unit_price, subtotal) VALUES
                                                                                                                ('PED-2026-001', 1, 'Cien Anos de Soledad', 'Gabriel Garcia Marquez', 2, 25000.00, 50000.00),
                                                                                                                ('PED-2026-001', 3, 'Pedro Paramo', 'Juan Rulfo', 1, 10000.00, 10000.00),
                                                                                                                ('PED-2026-002', 2, 'Rayuela', 'Julio Cortazar', 1, 45000.00, 45000.00),
                                                                                                                ('PED-2026-108', 1, 'Cien Anos de Soledad', 'Gabriel Garcia Marquez', 1, 25000.00, 25000.00),
                                                                                                                ('PED-2026-108', 2, 'Rayuela', 'Julio Cortazar', 1, 35000.00, 35000.00);