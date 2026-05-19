# E-Commerce Microservices Platform with Native SQL Reporting

A Spring Boot microservices showcase that demonstrates:

- Spring IoC / dependency injection across all layers
- service-specific PostgreSQL databases
- REST-based service-to-service communication
- native SQL reporting queries in the reporting-service
- Java Streams for aggregation and result shaping
- Docker Compose orchestration for local setup

## Services

- `product-service` (`:8081`) - product catalog, search, inventory validation/decrement
- `order-service` (`:8082`) - order placement and status tracking, calls product service
- `reporting-service` (`:8083`) - syncs completed orders from the order service and exposes reporting endpoints
- `api-gateway` (`:8080`) - routes `/api/products/**`, `/api/orders/**`, `/api/reports/**`

## Quick Start

### Prerequisites

- Docker + Docker Compose
- Java 17 and Maven (optional, for local build without containers)

### Run with Docker Compose

```bash
docker compose up --build
```

The platform exposes these ports:

- `8080` - API gateway
- `8081` - product-service
- `8082` - order-service
- `8083` - reporting-service
- `5433` - product PostgreSQL
- `5434` - order PostgreSQL
- `5435` - reporting PostgreSQL

## Reporting Service Endpoints

The reporting-service routes are exposed through the API gateway at `/api/reports`.

- `POST /api/reports/sync` - import completed orders into the reporting database
- `GET /api/reports/monthly-sales` - monthly sales grouped by customer and month
- `GET /api/reports/customer-spending` - total spending per customer
- `GET /api/reports/top-products-all-time` - all-time top selling products
- `GET /api/reports/past-month-top-products` - top products from the last 30 days
- `GET /api/reports/past-year-top-products` - top products from the last 12 months
- `GET /api/reports/top-products-by-month?month=YYYY-MM` - top products for a specific month

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

4. Query reporting endpoints:

```bash
curl http://localhost:8080/api/reports/monthly-sales
curl http://localhost:8080/api/reports/customer-spending
curl http://localhost:8080/api/reports/top-products-all-time
curl http://localhost:8080/api/reports/past-month-top-products
curl http://localhost:8080/api/reports/top-products-by-month?month=2026-05
```

## Reporting Service Highlights

- `reporting-service` syncs completed orders from `order-service` into its own reporting database
- uses native SQL queries for aggregation and ranking
- exposes multiple reporting views for monthly sales, customer spending, and top selling products

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
