package com.ecommerce.reporting.service;

import com.ecommerce.reporting.client.OrderServiceClient;
import com.ecommerce.reporting.dto.MonthlyCustomerSpending;
import com.ecommerce.reporting.dto.OrderSyncResponse;
import com.ecommerce.reporting.dto.ProductStat;
import com.ecommerce.reporting.model.OrderItemReportEntity;
import com.ecommerce.reporting.model.OrderReportEntity;
import com.ecommerce.reporting.repository.OrderItemReportRepository;
import com.ecommerce.reporting.repository.OrderReportRepository;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessReportService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneOffset.UTC);

    private final OrderReportRepository orderReportRepository;
    private final OrderItemReportRepository orderItemReportRepository;
    private final OrderServiceClient orderServiceClient;

    public BusinessReportService(OrderReportRepository orderReportRepository,
                                 OrderItemReportRepository orderItemReportRepository,
                                 OrderServiceClient orderServiceClient) {
        this.orderReportRepository = orderReportRepository;
        this.orderItemReportRepository = orderItemReportRepository;
        this.orderServiceClient = orderServiceClient;
    }

    @Transactional
    public OrderSyncResponse syncCompletedOrders() {
        List<OrderServiceClient.RemoteOrder> completedOrders = orderServiceClient.fetchCompletedOrders();

        int importedOrders = 0;
        int importedItems = 0;

        for (OrderServiceClient.RemoteOrder remoteOrder : completedOrders) {
            if (remoteOrder.id() == null || orderReportRepository.existsBySourceOrderId(remoteOrder.id())) {
                continue;
            }

            OrderReportEntity order = new OrderReportEntity();
            order.setSourceOrderId(remoteOrder.id());
            order.setCustomerId(remoteOrder.customerId());
            order.setStatus(remoteOrder.status());
            order.setTotalAmount(remoteOrder.totalAmount());
            order.setCreatedAt(Instant.parse(remoteOrder.createdAt()));
            orderReportRepository.save(order);
            importedOrders++;

            if (remoteOrder.items() != null) {
                for (OrderServiceClient.RemoteOrderItem item : remoteOrder.items()) {
                    OrderItemReportEntity reportItem = new OrderItemReportEntity();
                    reportItem.setSourceOrderId(remoteOrder.id());
                    reportItem.setProductId(item.productId());
                    reportItem.setProductName(item.productName());
                    reportItem.setQuantity(item.quantity());
                    reportItem.setUnitPrice(item.unitPrice());
                    orderItemReportRepository.save(reportItem);
                    importedItems++;
                }
            }
        }

        return new OrderSyncResponse(importedOrders, importedItems);
    }

    public Map<String, List<MonthlyCustomerSpending>> monthlySalesReport() {
        List<Object[]> rows = orderReportRepository.monthlySalesByCustomerNative();

        return rows.stream()
                .collect(Collectors.groupingBy(
                        row -> row[2].toString(),
                        Collectors.mapping(
                                row -> new MonthlyCustomerSpending(
                                        ((Number) row[0]).longValue(),
                                        ((Number) row[1]).doubleValue()),
                                Collectors.toList())
                ));
    }

    public Map<Long, Double> customerSpendingSummary() {
        List<OrderReportEntity> completedOrders = orderReportRepository.findCompletedOrdersNative();
        return completedOrders.stream()
                .collect(Collectors.groupingBy(OrderReportEntity::getCustomerId,
                        Collectors.summingDouble(OrderReportEntity::getTotalAmount)));
    }

    public List<ProductStat> topSellingProducts() {
        List<Object[]> rows = orderItemReportRepository.findTopSellingProductsNative();

        return rows.stream()
                .map(row -> new ProductStat(
                        ((Number) row[0]).longValue(),
                        String.valueOf(row[1]),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).longValue()))
                .filter(stat -> stat.totalSold() > 1)
                .sorted(Comparator.comparing(ProductStat::revenue).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<ProductStat> topSellingProductsPast30Days() {
        List<Object[]> rows = orderItemReportRepository.findTopSellingProductsPast30DaysNative();

        return rows.stream()
                .map(row -> new ProductStat(
                        ((Number) row[0]).longValue(),
                        String.valueOf(row[1]),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).longValue()))
                .filter(stat -> stat.totalSold() > 1)
                .sorted(Comparator.comparing(ProductStat::revenue).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<ProductStat> topSellingProductsPastYear() {
        List<Object[]> rows = orderItemReportRepository.findTopSellingProductsPast365DaysNative();

        return rows.stream()
                .map(row -> new ProductStat(
                        ((Number) row[0]).longValue(),
                        String.valueOf(row[1]),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).longValue()))
                .filter(stat -> stat.totalSold() > 1)
                .sorted(Comparator.comparing(ProductStat::revenue).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

        public List<ProductStat> topSellingProductsForMonth(String month) {
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        Instant start = yearMonth.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = yearMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<Object[]> rows = orderItemReportRepository.findTopSellingProductsByDateRangeNative(start, end);

        return rows.stream()
            .map(row -> new ProductStat(
                ((Number) row[0]).longValue(),
                String.valueOf(row[1]),
                ((Number) row[2]).longValue(),
                ((Number) row[3]).doubleValue(),
                ((Number) row[4]).longValue()))
            .filter(stat -> stat.totalSold() > 1)
            .sorted(Comparator.comparing(ProductStat::revenue).reversed())
            .limit(10)
            .collect(Collectors.toList());
        }
}
