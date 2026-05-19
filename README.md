# E-Commerce Microservices Platform with Native SQL Reporting

A Spring Boot microservices showcase that demonstrates:

- Spring IoC / dependency injection across all layers
- Java Streams for in-memory aggregations in reporting
- Native SQL (`@Query(nativeQuery = true)`) for advanced reporting queries
- Dockerized microservices + PostgreSQL per service
- REST-based service-to-service communication

## Services

- `product-service` (`:8081`) - product catalog, search, inventory validation/decrement
- `order-service` (`:8082`) - order placement and status tracking, calls product service
- `reporting-service` (`:8083`) - syncs completed orders, runs native SQL reports + streams
- `api-gateway` (`:8080`) - routes `/api/products/**`, `/api/orders/**`, `/api/reports/**`

## Quick Start

### Prerequisites

- Docker + Docker Compose
- Java 17 and Maven (for local non-container run)

### Run with Docker Compose

```bash
docker compose up --build
```

## Example API Flow

1. Create product via gateway:

```bash
curl -X POST http://localhost:8080/api/products   -H 'Content-Type: application/json'   -d '{"name":"Laptop","description":"Ultrabook","price":1200,"stock":15}'
```

2. Place order via gateway:

```bash
curl -X POST http://localhost:8080/api/orders   -H 'Content-Type: application/json'   -d '{
    "customerId": 101,
    "items": [
      {"productId":1,"productName":"Laptop","quantity":2,"unitPrice":1200}
    ]
  }'
```

3. Sync completed orders into reporting DB:

```bash
curl -X POST http://localhost:8080/api/reports/sync
```

4. Read reports:

```bash
curl http://localhost:8080/api/reports/monthly-sales
curl http://localhost:8080/api/reports/customer-spending
curl http://localhost:8080/api/reports/top-products
```

## Native SQL + Streams Highlight

`reporting-service` combines native SQL and streams:

- Native SQL retrieves monthly sales and top products using joins, grouping, and window functions.
- Java Streams perform in-memory grouping, sorting, filtering, and final shaping of report payloads.

## Project Structure

```text
.
├── api-gateway
├── product-service
├── order-service
├── reporting-service
├── docker-compose.yml
└── pom.xml
```
