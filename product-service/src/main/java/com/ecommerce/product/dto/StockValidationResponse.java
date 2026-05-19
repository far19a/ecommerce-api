package com.ecommerce.product.dto;

public record StockValidationResponse(
        Long productId,
        boolean available,
        String message
) {
}
