package com.ecommerce.reporting.dto;

public record MonthlyCustomerSpending(
        Long customerId,
        Double total
) {
}
