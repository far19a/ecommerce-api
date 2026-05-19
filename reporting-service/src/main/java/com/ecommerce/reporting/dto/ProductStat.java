package com.ecommerce.reporting.dto;

public record ProductStat(
        Long productId,
        String productName,
        Long totalSold,
        Double revenue,
        Long ranking
) {
}
