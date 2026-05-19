package com.ecommerce.reporting.client;

import java.util.List;

public interface OrderServiceClient {
    List<RemoteOrder> fetchCompletedOrders();

    record RemoteOrder(Long id, Long customerId, String status, Double totalAmount, String createdAt, List<RemoteOrderItem> items) {
    }

    record RemoteOrderItem(Long productId, String productName, Integer quantity, Double unitPrice) {
    }
}
