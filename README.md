# Microservicio de Órdenes - Orders

## Descripción General

**Orders** es un microservicio responsable de gestionar todas las operaciones relacionadas con órdenes de compra en un sistema de e-commerce. Implementa la lógica de negocio para crear, consultar y gestionar órdenes, integrándose con el microservicio **Catalogue** para validar disponibilidad de libros y gestionar el stock.

**Características principales:**
- ✅ Crear nuevas órdenes con validación de stock
- ✅ Consultar órdenes por usuario
- ✅ Ver detalles de items en una orden
- ✅ Integración en tiempo real con Catalogue para stock
- ✅ Rollback automático si falla la deducción de stock

---

## Arquitectura del Proyecto

```
Orders Microservice (Puerto 8081)
│
├── controllers/ (REST API)
│   └── OrderController
│       ├── GET  /api/v1/health                           (Health check)
│       ├── GET  /api/v1/orders/users/{userId}            (Listar órdenes del usuario)
│       ├── POST /api/v1/orders                           (Crear nueva orden)
│       ├── GET  /api/v1/orders/{orderId}/items           (Ver todos los items)
│       └── GET  /api/v1/orders/{orderId}/items/{itemId}  (Ver item específico)
│
├── service/
│   ├── OrderService                 (Lógica de negocio)
│   └── HealthService                (Estado del servicio)
│
├── repository/ (Acceso a datos)
│   ├── OrderRepository              (CRUD de órdenes)
│   ├── OrderItemRepository          (CRUD de items de orden)
│   └── OrderSequenceRepository      (Generador de IDs secuenciales)
│
├── entity/ (Modelos JPA)
│   ├── Order                        (Tabla: orders)
│   ├── OrderItem                    (Tabla: order_items)
│   └── OrderSequence                (Tabla: order_sequence)
│
├── client/ (HTTP Client)
│   ├── CatalogueClient              (Interface)
│   └── CatalogueClientImpl           (Implementación WebClient)
│       └── dto/
│           ├── CatalogueBookResponse
│           └── StockAdjustmentRequestDto
│
└── config/
    └── WebClientConfig              (Configuración de WebClient)
```
## Resumen de Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/health` | Health check del servicio |
| `GET` | `/api/v1/orders/users/{userId}` | Listar órdenes del usuario |
| `POST` | `/api/v1/orders` | Crear nueva orden (con validación de stock) |
| `GET` | `/api/v1/orders/{orderId}/items` | Obtener todos los items de una orden |
| `GET` | `/api/v1/orders/{orderId}/items/{itemId}` | Obtener item específico |

---

## Integración con Microservicios

### Catalogue Microservice (Puerto 8085)

Orders **consume** los siguientes endpoints de Catalogue:

#### 1. Obtener detalle de libro
```
GET http://localhost:8085/api/v1/internal/books/{id}/detail
```
**Propósito**: Validar que el libro existe, está disponible y obtener precio/stock  
**Utilizado en**: Crear orden  
**Response**:
```json
{
  "id": 5,
  "title": "Clean Code",
  "description": "Guía de código limpio",
  "author": "Robert C. Martin",
  "price": 45.99,
  "stock": 50,
  "isActive": true
}
```

#### 2. Descontar stock
```
POST http://localhost:8085/api/v1/internal/books/stock/decrease
Content-Type: application/json

{
  "books": [
    { "id": 5, "quantity": 2 },
    { "id": 8, "quantity": 1 }
  ]
}
```
**Propósito**: Restar cantidad de libros del stock  
**Utilizado en**: Después de crear orden (en mismo endpoint)  
**Validación**: Si falla → Orden se elimina (rollback)

#### 3. Aumentar stock (futuro - para devoluciones)
```
POST http://localhost:8085/api/v1/internal/books/stock/increase
Content-Type: application/json

{
  "books": [
    { "id": 5, "quantity": 2 }
  ]
}
```
**Propósito**: Adicionar cantidad de libros al stock  
**Utilizado en**: Procesar devoluciones o cancelaciones

---

## 📡 API REST - Endpoints Implementados

### Base URL
```
http://localhost:8081
```

---

### 1️⃣ Health Check
```http
GET /api/v1/health
```

**Descripción**: Verifica que el servicio está activo y conectado a BD  
**Autenticación**: No requerida  
**Response (200 OK)**:
```json
"Orders Microservice is running"
```

---

### 2️⃣ Crear Nueva Orden
```http
POST /api/v1/orders
Content-Type: application/json
```

**Descripción**: Crea una nueva orden con validación de stock y disponibilidad de libros  

**Request Body**:
```json
{
  "userId": "user-123",
  "items": [
    {
      "bookId": 5,
      "quantity": 2
    },
    {
      "bookId": 8,
      "quantity": 1
    }
  ]
}
```

**Response (201 Created)**:
```json
{
  "id": "PED-2026-150",
  "userId": "user-123",
  "fecha": "23/05/2026",
  "total": 146.47,
  "estado": "Procesando"
}
```

**Posibles Errores**:
- `400 Bad Request`: userId vacío, items vacío, cantidad inválida
- `404 Not Found`: Algún libro no existe en Catalogue
- `409 Conflict`: Libro no disponible o stock insuficiente
- `500 Internal Server Error`: Falla al descontar stock (orden cancelada automáticamente)

**Flujo interno**:
1. Validar userId y items
2. Consultar cada libro en Catalogue
3. Validar disponibilidad y stock
4. Guardar orden con estado "Procesando"
5. Guardar items con snapshot de libros
6. **Descontar stock en Catalogue**
7. Si falla paso 6 → Eliminar orden y lanzar error

---

### 3️⃣ Listar Órdenes por Usuario
```http
GET /api/v1/orders/users/{userId}
```

**Descripción**: Obtiene todas las órdenes del usuario, ordenadas por fecha descendente  
**Parámetros**:
- `userId` (path): ID del usuario

**Response (200 OK)**:
```json
[
  {
    "id": "PED-2026-150",
    "userId": "user-123",
    "fecha": "23/05/2026",
    "total": 146.47,
    "estado": "Procesando"
  },
  {
    "id": "PED-2026-001",
    "userId": "user-123",
    "fecha": "05/01/2026",
    "total": 60000.00,
    "estado": "Entregado"
  }
]
```

**Posibles Errores**:
- `200 OK` con lista vacía: Usuario sin órdenes

---

### 4️⃣ Obtener Todos los Items de una Orden
```http
GET /api/v1/orders/{orderId}/items
```

**Descripción**: Obtiene todos los libros comprados en una orden específica  
**Parámetros**:
- `orderId` (path): ID de la orden (ej: PED-2026-150)

**Response (200 OK)**:
```json
[
  {
    "id": 1,
    "orderId": "PED-2026-150",
    "bookId": 5,
    "bookTitle": "Clean Code",
    "bookAuthor": "Robert C. Martin",
    "quantity": 2,
    "unitPrice": 45.99,
    "subtotal": 91.98,
    "fechaCreacion": "2026-05-23T10:30:00"
  },
  {
    "id": 2,
    "orderId": "PED-2026-150",
    "bookId": 8,
    "bookTitle": "Design Patterns",
    "bookAuthor": "Gang of Four",
    "quantity": 1,
    "unitPrice": 55.50,
    "subtotal": 55.50,
    "fechaCreacion": "2026-05-23T10:30:00"
  }
]
```

**Posibles Errores**:
- `404 Not Found`: Orden no existe

---

### Obtener Item Específico de una Orden
```http
GET /api/v1/orders/{orderId}/items/{itemId}
```

**Descripción**: Obtiene un item específico de una orden (validando que pertenece a esa orden)  
**Parámetros**:
- `orderId` (path): ID de la orden
- `itemId` (path): ID del item en esa orden

**Response (200 OK)**:
```json
{
  "id": 2,
  "orderId": "PED-2026-150",
  "bookId": 8,
  "bookTitle": "Design Patterns",
  "bookAuthor": "Gang of Four",
  "quantity": 1,
  "unitPrice": 55.50,
  "subtotal": 55.50,
  "fechaCreacion": "2026-05-23T10:30:00"
}
```

**Posibles Errores**:
- `404 Not Found`: Orden o item no existe
- `403 Forbidden`: El item no pertenece a esta orden

---

## Estructura de Base de Datos

### Conexión
```
Host: localhost
Port: 3307
Database: orders
Usuario: root
Password: Unir1234
```

---

### 📊 Tabla: `orders`
Almacena el resumen de cada orden realizada por usuarios.

```sql
CREATE TABLE orders (
  id VARCHAR(20) PRIMARY KEY,           -- Identificador único (PED-YYYY-XXX)
  user_id VARCHAR(20) NOT NULL,         -- Usuario que realizó la compra
  fecha DATE NOT NULL,                  -- Fecha de la orden
  total DECIMAL(12, 2) NOT NULL,        -- Monto total de la orden
  estado VARCHAR(50) NOT NULL,          -- Estado: Procesando, Entregado, etc.
  fecha_creacion TIMESTAMP,             -- Timestamp de creación
  INDEX idx_orders_user_fecha (user_id, fecha)
);
```

**Ejemplo de datos**:
```sql
INSERT INTO orders VALUES 
  ('PED-2026-001', 'user-1', '2026-01-05', 60000.00, 'Entregado', NOW()),
  ('PED-2026-002', 'user-2', '2026-01-08', 45000.00, 'Procesando', NOW());
```

---

### 📦 Tabla: `order_items`
Almacena el detalle de cada libro en una orden. **Actúa como snapshot histórico** de la compra en el momento exacto.

```sql
CREATE TABLE order_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- Identificador único del item
  order_id VARCHAR(20) NOT NULL,           -- FK: Referencia a orders(id)
  book_id BIGINT NOT NULL,                 -- ID del libro (referencia a Catalogue)
  book_title VARCHAR(255) NOT NULL,        -- Título del libro (snapshot)
  book_author VARCHAR(255),                -- Autor (snapshot)
  quantity INT NOT NULL,                   -- Cantidad comprada
  unit_price DECIMAL(12, 2) NOT NULL,      -- Precio unitario al momento de compra
  subtotal DECIMAL(12, 2) NOT NULL,        -- Subtotal = quantity * unit_price
  fecha_creacion TIMESTAMP,                -- Timestamp de creación
  
  CONSTRAINT fk_order_items_order 
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  
  CONSTRAINT uk_order_items_order_book 
    UNIQUE (order_id, book_id),            -- Un libro no puede estar 2 veces en misma orden
  
  CONSTRAINT chk_order_items_quantity 
    CHECK (quantity > 0),                  -- La cantidad debe ser positiva
  
  CONSTRAINT chk_order_items_unit_price 
    CHECK (unit_price >= 0),               -- El precio no puede ser negativo
  
  INDEX idx_order_items_order_id (order_id),
  INDEX idx_order_items_book_id (book_id)
);
```

**Ejemplo de datos**:
```sql
INSERT INTO order_items VALUES 
  (1, 'PED-2026-001', 1, 'Cien Años de Soledad', 'Gabriel García Márquez', 
   2, 25000.00, 50000.00, NOW()),
  (2, 'PED-2026-001', 3, 'Pedro Páramo', 'Juan Rulfo', 
   1, 10000.00, 10000.00, NOW());
```

--

### Tabla: `order_sequence`
Genera IDs secuenciales para mantener numeración consecutiva de órdenes.

```sql
CREATE TABLE order_sequence (
  id BIGINT AUTO_INCREMENT PRIMARY KEY
);

ALTER TABLE order_sequence AUTO_INCREMENT = 150;
```

**Propósito**: Generar números consecutivos ej: PED-2026-001, PED-2026-002, etc.  
**Formato**: `PED-{AÑO}-{SECUENCIA_3_DÍGITOS}`

---

## 🗂️ Diagrama de Relaciones

```
┌─────────────────┐
│     orders      │
├─────────────────┤
│ id (PK)         │◄────┐
│ user_id         │     │
│ fecha           │     │ ON DELETE CASCADE
│ total           │     │
│ estado          │     │
└─────────────────┘     │
                        │
                        │ FK
                        │
                ┌───────────────────┐
                │   order_items     │
                ├───────────────────┤
                │ id (PK)           │
                │ order_id (FK)     ├────┘
                │ book_id           │ (apunta a Catalogue externamente)
                │ book_title        │
                │ book_author       │
                │ quantity          │
                │ unit_price        │
                │ subtotal          │
                └───────────────────┘

┌─────────────────────┐
│  order_sequence     │
├─────────────────────┤
│ id (AUTO_INCREMENT) │ → Genera secuencias para PED-2026-XXX
└─────────────────────┘
```

---

## Configuración y Ejecución

### Prerequisitos
- ✅ Java 25
- ✅ Maven 3.8+
- ✅ MySQL 8.0+
- ✅ Microservicio Catalogue ejecutándose en puerto 8085

### 1. Crear Base de Datos
```bash
mysql -u root -p < src/main/resources/DB/orders.sql
```

### 2. Compilar el Proyecto
```bash
./mvnw clean compile
```

### 3. Ejecutar el Servicio
```bash
./mvnw spring-boot:run
```

El servicio estará disponible en: `http://localhost:8081`

### 4. Verificar Salud
```bash
curl http://localhost:8081/api/v1/health
```
---


**Última actualización**: Mayo 23, 2026  
**Versión**: 0.0.1-SNAPSHOT

