package com.ecommerce.reporting.dto;

public record OrderSyncResponse(
        int importedOrders,
        int importedItems
) {
}
