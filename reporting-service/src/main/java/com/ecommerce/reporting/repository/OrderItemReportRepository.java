package com.ecommerce.reporting.repository;

import com.ecommerce.reporting.model.OrderItemReportEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemReportRepository extends JpaRepository<OrderItemReportEntity, Long> {

    @Query(value = """
            SELECT oi.product_id, oi.product_name, 
                   SUM(oi.quantity) AS total_sold,
                   SUM(oi.quantity * oi.unit_price) AS revenue,
                   RANK() OVER (ORDER BY SUM(oi.quantity) DESC) AS ranking
            FROM order_items oi
            JOIN orders o ON oi.source_order_id = o.source_order_id
            WHERE o.status = 'COMPLETED'
            GROUP BY oi.product_id, oi.product_name
            HAVING SUM(oi.quantity) > 1
            """, nativeQuery = true)
    List<Object[]> findTopSellingProductsNative();

    @Query(value = """
            SELECT oi.product_id, oi.product_name, 
                   SUM(oi.quantity) AS total_sold,
                   SUM(oi.quantity * oi.unit_price) AS revenue,
                   RANK() OVER (ORDER BY SUM(oi.quantity) DESC) AS ranking
            FROM order_items oi
            JOIN orders o ON oi.source_order_id = o.source_order_id
            WHERE o.status = 'COMPLETED'
              AND o.created_at > NOW() - INTERVAL '30 days'
            GROUP BY oi.product_id, oi.product_name
            ORDER BY total_sold DESC
            HAVING SUM(oi.quantity) > 1
            """, nativeQuery = true)
    List<Object[]> findTopSellingProductsPast30DaysNative();

    @Query(value = """
            SELECT oi.product_id, oi.product_name, 
                   SUM(oi.quantity) AS total_sold,
                   SUM(oi.quantity * oi.unit_price) AS revenue,
                   RANK() OVER (ORDER BY SUM(oi.quantity) DESC) AS ranking
            FROM order_items oi
            JOIN orders o ON oi.source_order_id = o.source_order_id
            WHERE o.status = 'COMPLETED'
              AND o.created_at > NOW() - INTERVAL '365 days'
            GROUP BY oi.product_id, oi.product_name
            ORDER BY total_sold DESC
            HAVING SUM(oi.quantity) > 1
            """, nativeQuery = true)
    List<Object[]> findTopSellingProductsPast365DaysNative();

    @Query(value = """
            SELECT oi.product_id, oi.product_name, 
                   SUM(oi.quantity) AS total_sold,
                   SUM(oi.quantity * oi.unit_price) AS revenue,
                   RANK() OVER (ORDER BY SUM(oi.quantity) DESC) AS ranking
            FROM order_items oi
            JOIN orders o ON oi.source_order_id = o.source_order_id
            WHERE o.status = 'COMPLETED'
              AND o.created_at >= :start
              AND o.created_at < :end
            GROUP BY oi.product_id, oi.product_name
            ORDER BY total_sold DESC
            HAVING SUM(oi.quantity) > 1
            """, nativeQuery = true)
    List<Object[]> findTopSellingProductsByDateRangeNative(Instant start, Instant end);
}
