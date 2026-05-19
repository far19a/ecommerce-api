package com.ecommerce.order.repository;

import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.model.PurchaseOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByStatus(OrderStatus status);
}
