package com.customerorders.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateOrderRequest {

    @NotNull(message = "orderDate is required (YYYY-MM-DD)")
    private LocalDate orderDate;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private BigDecimal amount;

    public CreateOrderRequest() {}

    public LocalDate getOrderDate() { return orderDate; }
    public BigDecimal getAmount() { return amount; }

    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
