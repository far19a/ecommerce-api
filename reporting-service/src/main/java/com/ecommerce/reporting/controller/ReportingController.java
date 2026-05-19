package com.ecommerce.reporting.controller;

import com.ecommerce.reporting.dto.MonthlyCustomerSpending;
import com.ecommerce.reporting.dto.OrderSyncResponse;
import com.ecommerce.reporting.dto.ProductStat;
import com.ecommerce.reporting.service.BusinessReportService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportingController {

    private final BusinessReportService businessReportService;

    public ReportingController(BusinessReportService businessReportService) {
        this.businessReportService = businessReportService;
    }

    @PostMapping("/sync")
    public OrderSyncResponse syncOrders() {
        return businessReportService.syncCompletedOrders();
    }

    @GetMapping("/monthly-sales")
    public Map<String, List<MonthlyCustomerSpending>> monthlySales() {
        return businessReportService.monthlySalesReport();
    }

    @GetMapping("/customer-spending")
    public Map<Long, Double> customerSpending() {
        return businessReportService.customerSpendingSummary();
    }

    @GetMapping("/top-products-all-time")
    public List<ProductStat> topProductsAllTime() {
        return businessReportService.topSellingProducts();
    }

    @GetMapping("/past-month-top-products")
    public List<ProductStat> topProductsPast30Days() {
        return businessReportService.topSellingProductsPast30Days();
    }

    @GetMapping("/past-year-top-products")
    public List<ProductStat> topProductsPastYear() {
        return businessReportService.topSellingProductsPastYear();
    }

    @GetMapping("/top-products-by-month")
    public List<ProductStat> topProductsByMonth(@RequestParam("month") String month) {
        return businessReportService.topSellingProductsForMonth(month);
    }
}
