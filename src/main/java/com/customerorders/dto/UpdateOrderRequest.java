package com.customerorders.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateOrderRequest {

    @NotNull(message = "orderDate is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate orderDate;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private BigDecimal amount;

    public UpdateOrderRequest() {}

    public UpdateOrderRequest(LocalDate orderDate, BigDecimal amount) {
        this.orderDate = orderDate;
        this.amount = amount;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}