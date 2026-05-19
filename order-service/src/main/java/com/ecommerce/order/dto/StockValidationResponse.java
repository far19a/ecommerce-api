package com.ecommerce.order.dto;

public record StockValidationResponse(
        Long productId,
        boolean available,
        String message
) {
}
