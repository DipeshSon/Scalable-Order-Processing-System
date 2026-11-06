# Scalable Order Processing System

A microservices-based order processing application built using **Java, Spring Boot, Apache Kafka, MySQL, Redis, and Docker**.

The system demonstrates asynchronous communication between microservices, inventory management, event-driven architecture, and duplicate order processing prevention.

## Project Overview

The Scalable Order Processing System is designed to manage customer orders and inventory through independent microservices.

When a customer places an order:

1. The Order Service receives the request.
2. The order is saved in the MySQL database.
3. An `order-created` event is published to Apache Kafka.
4. The Inventory Service consumes the event and checks available stock.
5. Inventory is updated in MySQL and Redis.
6. The Inventory Service publishes an order status event.
7. The Order Service consumes the status event and updates the order status.

The system also implements idempotency to help prevent duplicate processing of the same order.

## Architecture

```text
                 Client / Postman
                        |
                        v
                +---------------+
                | Order Service |
                | Spring Boot   |
                +---------------+
                        |
               Save Order in MySQL
                        |
                        v
                 Apache Kafka
                  order-created
                        |
                        v
              +-------------------+
              | Inventory Service |
              |   Spring Boot     |
              +-------------------+
                   |          |
                   v          v
                 MySQL       Redis
                   |
                   v
              Apache Kafka
               order-status
                   |
                   v
              Order Service
                   |
                   v
            Update Order Status
```

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend programming |
| Spring Boot | Microservices development |
| Spring Data JPA / Hibernate | Database operations |
| MySQL | Persistent data storage |
| Apache Kafka | Asynchronous communication |
| Redis | Inventory data caching |
| Docker | Containerization |
| Docker Compose | Running multiple services |
| Maven | Build and dependency management |
| Postman | API testing |

## Microservices

### 1. Order Service

**Port:** `8080`

Responsibilities:
- Create new orders.
- Retrieve an order by ID.
- Retrieve all orders.
- Store order details in MySQL.
- Publish order-created events to Kafka.
- Consume order-status events and update order status.

### 2. Inventory Service

**Port:** `8081`

Responsibilities:
- Consume order-created events from Kafka.
- Check and reduce available product stock.
- Store inventory information in MySQL.
- Update inventory data in Redis.
- Prevent duplicate order processing using an idempotency record.
- Publish order status events to Kafka.

## API Endpoints

### Order Service

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/orders` | Create a new order |
| GET | `/orders/{id}` | Retrieve an order by ID |
| GET | `/orders` | Retrieve all orders |

### Create Order

**Request:**

```http
POST http://localhost:8080/orders
Content-Type: application/json
```

**Request Body:**

```json
{
  "userId": 1,
  "productId": 5005,
  "quantity": 2
}
```

The request creates an order and initiates asynchronous inventory processing through Kafka.

### Get Order by ID

```http
GET http://localhost:8080/orders/1
```

### Get All Orders

```http
GET http://localhost:8080/orders
```

## Docker Setup

### Prerequisites

Install the following:

- Java 21 or compatible configured project JDK
- Maven
- Docker Desktop
- MySQL

### 1. Clone the Repository

```bash
git clone https://github.com/DipeshSon/Scalable-Order-Processing-System.git
```

```bash
cd Scalable-Order-Processing-System
```

### 2. Configure Environment Variables

Create a `.env` file in the project root.

Add your own local database credentials:

```properties
ORDER_DB_URL=jdbc:mysql://host.docker.internal:3306/order_db
INVENTORY_DB_URL=jdbc:mysql://host.docker.internal:3306/inventory_db
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

Make sure both MySQL databases exist.

**Important:** Never commit your `.env` file or real database credentials to GitHub.

### 3. Build Docker Images

Build the Order Service image:

```bash
docker build -t order-service:1.0 .
```

Build the Inventory Service image:

```bash
docker build -t inventory-service:1.0 ./inventory-service
```

### 4. Start All Services

```bash
docker compose up -d
```

### 5. Verify Running Containers

```bash
docker compose ps
```

### 6. View Application Logs

Order Service:

```bash
docker logs -f order-service
```

Inventory Service:

```bash
docker logs -f inventory-service
```

Kafka:

```bash
docker logs -f order-kafka
```

### 7. Stop All Services

```bash
docker compose down
```

## Kafka Topics

| Topic | Purpose |
|---|---|
| `order-created` | Sends newly created order events to Inventory Service |
| `order-status` | Sends inventory processing results back to Order Service |

## Key Features

- Microservices architecture
- Event-driven communication using Apache Kafka
- Asynchronous order processing
- MySQL database integration
- Redis caching for inventory data
- Idempotency to prevent duplicate order processing
- Docker-based service deployment
- Independent Order and Inventory services

## Project Structure

```text
Scalable-Order-Processing-System/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── ...
│       └── resources/
│           └── application.properties
│
├── inventory-service/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── ...
│           └── resources/
│               └── application.properties
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .env.example
├── .gitignore
└── README.md
```

## Testing and Verification

The following setup checks have been performed during development:

- Docker Compose configuration validation.
- Kafka, Redis, Order Service, and Inventory Service containers started.
- Redis connectivity checked.
- Inventory data operations tested during development.

Full end-to-end order processing and failure scenarios should be verified before considering the system production-ready.

## Future Improvements

- Implement an API Gateway.
- Add Spring Security and JWT authentication.
- Add centralized logging and monitoring.
- Implement automated unit and integration tests.
- Add service health checks and retry mechanisms.
- Add performance testing using JMeter or k6.

## Author

**Dipesh Ravindra Sonawane**

B.Tech – Electronics and Telecommunication Engineering

GitHub: [DipeshSon](https://github.com/DipeshSon)

Project Repository: [Scalable Order Processing System](https://github.com/DipeshSon/Scalable-Order-Processing-System)

---

If you find this project useful, feel free to explore the repository and share your feedback.
