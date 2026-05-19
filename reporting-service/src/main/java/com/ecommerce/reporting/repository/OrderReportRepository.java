package com.ecommerce.reporting.repository;

import com.ecommerce.reporting.model.OrderReportEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderReportRepository extends JpaRepository<OrderReportEntity, Long> {

    boolean existsBySourceOrderId(Long sourceOrderId);

    @Query(value = """
            SELECT o.customer_id, SUM(o.total_amount) AS total,
                   DATE_TRUNC('month', o.created_at) AS month
            FROM orders o
            WHERE o.status = 'COMPLETED'
            GROUP BY month, o.customer_id
            ORDER BY month DESC
            """, nativeQuery = true)
    List<Object[]> monthlySalesByCustomerNative();

    @Query(value = """
            SELECT * FROM orders o
            WHERE o.status = 'COMPLETED'
            """, nativeQuery = true)
    List<OrderReportEntity> findCompletedOrdersNative();
}
