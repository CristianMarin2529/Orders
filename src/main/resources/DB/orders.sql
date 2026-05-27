CREATE DATABASE IF NOT EXISTS orders;

USE orders;

-- =========================================================
-- TABLA DE ORDENES
-- =========================================================
CREATE TABLE IF NOT EXISTS orders (
                                      id VARCHAR(20) PRIMARY KEY,
                                      user_id VARCHAR(20) NOT NULL,
                                      fecha DATE NOT NULL,
                                      total DECIMAL(12,2) NOT NULL,
                                      estado VARCHAR(50) NOT NULL,
                                      fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                      INDEX idx_orders_user_fecha (user_id, fecha)
);

-- =========================================================
-- TABLA DETALLE DE ORDENES
-- Guarda una foto histórica del libro al momento de compra
-- =========================================================
CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           order_id VARCHAR(20) NOT NULL,
                                           book_id BIGINT NOT NULL,
                                           book_title VARCHAR(255) NOT NULL,
                                           book_author VARCHAR(255),
                                           quantity INT NOT NULL,
                                           unit_price DECIMAL(12,2) NOT NULL,
                                           subtotal DECIMAL(12,2) NOT NULL,
                                           fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                           CONSTRAINT fk_order_items_order
                                               FOREIGN KEY (order_id)
                                                   REFERENCES orders(id)
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

-- =========================================================
-- INSERTAR ORDENES
-- =========================================================
INSERT INTO orders (id, user_id, fecha, total, estado) VALUES
                                                           ('PED-2026-001', 'user-1',  '2026-01-05',  60000.00, 'Entregado'),
                                                           ('PED-2026-002', 'user-2',  '2026-01-08',  45000.00, 'Procesando'),
                                                           ('PED-2026-003', 'user-3',  '2026-01-12',  75000.00, 'Cancelado'),
                                                           ('PED-2026-004', 'user-4',  '2026-01-15',  32000.00, 'Entregado'),
                                                           ('PED-2026-005', 'user-5',  '2026-01-18',  88000.00, 'En Transito'),
                                                           ('PED-2026-006', 'user-6',  '2026-02-02',  52000.00, 'Entregado'),
                                                           ('PED-2026-007', 'user-7',  '2026-02-05',  65000.00, 'Procesando'),
                                                           ('PED-2026-008', 'user-8',  '2026-02-10',  41000.00, 'Entregado'),
                                                           ('PED-2026-009', 'user-9',  '2026-02-14',  95000.00, 'En Transito'),
                                                           ('PED-2026-010', 'user-10', '2026-02-20',  28000.00, 'Procesando'),
                                                           ('PED-2026-011', 'user-11', '2026-03-05',  72000.00, 'Entregado'),
                                                           ('PED-2026-012', 'user-12', '2026-03-10',  58000.00, 'Cancelado'),
                                                           ('PED-2026-013', 'user-13', '2026-03-15',  46000.00, 'En Transito'),
                                                           ('PED-2026-014', 'user-14', '2026-03-20',  81000.00, 'Entregado'),
                                                           ('PED-2026-015', 'user-15', '2026-03-25',  39000.00, 'Procesando'),
                                                           ('PED-2026-016', 'user-16', '2026-04-02',  67000.00, 'Entregado'),
                                                           ('PED-2026-017', 'user-17', '2026-04-08',  54000.00, 'En Transito'),
                                                           ('PED-2026-018', 'user-18', '2026-04-15',  71000.00, 'Entregado'),
                                                           ('PED-2026-019', 'user-19', '2026-04-22',  33000.00, 'Procesando'),
                                                           ('PED-2026-020', 'user-20', '2026-04-28',  60000.00, 'Entregado'),
                                                           ('PED-2026-021', 'user-21', '2026-05-01',  99000.00, 'Entregado'),
                                                           ('PED-2026-022', 'user-22', '2026-05-03',  54000.00, 'Procesando'),
                                                           ('PED-2026-023', 'user-23', '2026-05-05', 120000.00, 'En Transito'),
                                                           ('PED-2026-024', 'user-24', '2026-05-06',  43000.00, 'Entregado'),
                                                           ('PED-2026-025', 'user-25', '2026-05-08',  88000.00, 'Cancelado'),
                                                           ('PED-2026-026', 'user-26', '2026-05-10',  47000.00, 'Procesando'),
                                                           ('PED-2026-027', 'user-27', '2026-05-12',  69000.00, 'Entregado'),
                                                           ('PED-2026-028', 'user-28', '2026-05-14', 105000.00, 'En Transito'),
                                                           ('PED-2026-029', 'user-29', '2026-05-16',  36000.00, 'Entregado'),
                                                           ('PED-2026-030', 'user-30', '2026-05-18',  78000.00, 'Procesando');

-- =========================================================
-- INSERTAR DETALLES DE ORDENES
-- =========================================================
INSERT INTO order_items (
    order_id,
    book_id,
    book_title,
    book_author,
    quantity,
    unit_price,
    subtotal
) VALUES

-- PED-2026-001
('PED-2026-001', 1, 'Cien Anos de Soledad', 'Gabriel Garcia Marquez', 2, 25000.00, 50000.00),
('PED-2026-001', 2, 'Pedro Paramo', 'Juan Rulfo', 1, 10000.00, 10000.00),

-- PED-2026-002
('PED-2026-002', 3, 'Rayuela', 'Julio Cortazar', 1, 45000.00, 45000.00),

-- PED-2026-003
('PED-2026-003', 4, 'El Aleph', 'Jorge Luis Borges', 3, 25000.00, 75000.00),

-- PED-2026-004
('PED-2026-004', 5, 'La Metamorfosis', 'Franz Kafka', 1, 32000.00, 32000.00),

-- PED-2026-005
('PED-2026-005', 6, '1984', 'George Orwell', 2, 44000.00, 88000.00),

-- PED-2026-006
('PED-2026-006', 7, 'Fahrenheit 451', 'Ray Bradbury', 1, 52000.00, 52000.00),

-- PED-2026-007
('PED-2026-007', 8, 'Don Quijote', 'Miguel de Cervantes', 1, 65000.00, 65000.00),

-- PED-2026-008
('PED-2026-008', 9, 'Crimen y Castigo', 'Fiodor Dostoievski', 1, 41000.00, 41000.00),

-- PED-2026-009
('PED-2026-009', 10, 'Ulises', 'James Joyce', 1, 95000.00, 95000.00),

-- PED-2026-010
('PED-2026-010', 11, 'La Iliada', 'Homero', 1, 28000.00, 28000.00),

-- PED-2026-011
('PED-2026-011', 12, 'La Odisea', 'Homero', 2, 36000.00, 72000.00),

-- PED-2026-012
('PED-2026-012', 13, 'Moby Dick', 'Herman Melville', 1, 58000.00, 58000.00),

-- PED-2026-013
('PED-2026-013', 14, 'Dracula', 'Bram Stoker', 1, 46000.00, 46000.00),

-- PED-2026-014
('PED-2026-014', 15, 'Hamlet', 'William Shakespeare', 3, 27000.00, 81000.00),

-- PED-2026-015
('PED-2026-015', 16, 'Macbeth', 'William Shakespeare', 1, 39000.00, 39000.00),

-- PED-2026-016
('PED-2026-016', 17, 'Orgullo y Prejuicio', 'Jane Austen', 1, 67000.00, 67000.00),

-- PED-2026-017
('PED-2026-017', 18, 'Jane Eyre', 'Charlotte Bronte', 1, 54000.00, 54000.00),

-- PED-2026-018
('PED-2026-018', 19, 'Frankenstein', 'Mary Shelley', 2, 35500.00, 71000.00),

-- PED-2026-019
('PED-2026-019', 20, 'El Principito', 'Antoine de Saint-Exupery', 1, 33000.00, 33000.00),

-- PED-2026-020
('PED-2026-020', 1, 'Cien Anos de Soledad', 'Gabriel Garcia Marquez', 1, 25000.00, 25000.00),
('PED-2026-020', 3, 'Rayuela', 'Julio Cortazar', 1, 35000.00, 35000.00),

-- PED-2026-021
('PED-2026-021', 21, 'El Hobbit', 'J.R.R. Tolkien', 3, 33000.00, 99000.00),

-- PED-2026-022
('PED-2026-022', 22, 'El Senor de los Anillos', 'J.R.R. Tolkien', 1, 54000.00, 54000.00),

-- PED-2026-023
('PED-2026-023', 23, 'Harry Potter y la Piedra Filosofal', 'J.K. Rowling', 2, 60000.00, 120000.00),

-- PED-2026-024
('PED-2026-024', 24, 'Los Juegos del Hambre', 'Suzanne Collins', 1, 43000.00, 43000.00),

-- PED-2026-025
('PED-2026-025', 25, 'Dune', 'Frank Herbert', 2, 44000.00, 88000.00),

-- PED-2026-026
('PED-2026-026', 26, 'It', 'Stephen King', 1, 47000.00, 47000.00),

-- PED-2026-027
('PED-2026-027', 27, 'Carrie', 'Stephen King', 1, 69000.00, 69000.00),

-- PED-2026-028
('PED-2026-028', 28, 'Neuromante', 'William Gibson', 3, 35000.00, 105000.00),

-- PED-2026-029
('PED-2026-029', 29, 'Fundacion', 'Isaac Asimov', 1, 36000.00, 36000.00),

-- PED-2026-030
('PED-2026-030', 30, 'Solaris', 'Stanislaw Lem', 2, 39000.00, 78000.00);