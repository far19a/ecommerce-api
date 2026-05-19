package com.ecommerce.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockValidationRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
) {
}
