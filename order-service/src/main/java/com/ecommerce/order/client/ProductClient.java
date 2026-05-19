package com.ecommerce.order.client;

import com.ecommerce.order.dto.StockValidationResponse;

public interface ProductClient {
    StockValidationResponse validateStock(Long productId, int quantity);

    void decrementStock(Long productId, int quantity);
}
