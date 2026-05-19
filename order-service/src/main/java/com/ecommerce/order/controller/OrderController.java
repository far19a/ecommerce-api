package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderCreateRequest;
import com.ecommerce.order.model.PurchaseOrder;
import com.ecommerce.order.service.OrderManagementService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderManagementService orderManagementService;

    public OrderController(OrderManagementService orderManagementService) {
        this.orderManagementService = orderManagementService;
    }

    @PostMapping
    public PurchaseOrder placeOrder(@Valid @RequestBody OrderCreateRequest request) {
        return orderManagementService.placeOrder(request);
    }

    @GetMapping
    public List<PurchaseOrder> listOrders() {
        return orderManagementService.listAll();
    }

    @GetMapping("/{id}")
    public PurchaseOrder getOrder(@PathVariable Long id) {
        return orderManagementService.getById(id);
    }

    @GetMapping("/completed")
    public List<PurchaseOrder> completedOrders() {
        return orderManagementService.listCompleted();
    }
}
