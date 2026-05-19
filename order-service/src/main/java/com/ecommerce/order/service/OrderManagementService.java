package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderCreateRequest;
import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.dto.StockValidationResponse;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.model.PurchaseOrder;
import com.ecommerce.order.repository.PurchaseOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderManagementService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductClient productClient;

    public OrderManagementService(PurchaseOrderRepository purchaseOrderRepository, ProductClient productClient) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productClient = productClient;
    }

    @Transactional
    public PurchaseOrder placeOrder(OrderCreateRequest request) {
        PurchaseOrder order = new PurchaseOrder();
        order.setCustomerId(request.customerId());
        order.setCreatedAt(Instant.now());
        order.setStatus(OrderStatus.PENDING);

        double total = 0;
        for (OrderItemRequest itemRequest : request.items()) {
            StockValidationResponse stock = productClient.validateStock(itemRequest.productId(), itemRequest.quantity());
            if (stock == null || !stock.available()) {
                order.setStatus(OrderStatus.FAILED);
                order.setTotalAmount(0D);
                return purchaseOrderRepository.save(order);
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemRequest.productId());
            item.setProductName(itemRequest.productName());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(itemRequest.unitPrice());
            order.getItems().add(item);
            total += itemRequest.quantity() * itemRequest.unitPrice();
        }

        request.items().forEach(i -> productClient.decrementStock(i.productId(), i.quantity()));
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.COMPLETED);

        return purchaseOrderRepository.save(order);
    }

    public PurchaseOrder getById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    public List<PurchaseOrder> listAll() {
        return purchaseOrderRepository.findAll();
    }

    public List<PurchaseOrder> listCompleted() {
        return purchaseOrderRepository.findByStatus(OrderStatus.COMPLETED);
    }
}
