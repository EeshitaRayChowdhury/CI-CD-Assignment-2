package com.customerorders.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderDTO {
    private Long id;
    private LocalDate orderDate;
    private BigDecimal amount;

    public OrderDTO() {}

    public OrderDTO(Long id, LocalDate orderDate, BigDecimal amount) {
        this.id = id;
        this.orderDate = orderDate;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public LocalDate getOrderDate() { return orderDate; }
    public BigDecimal getAmount() { return amount; }

    public void setId(Long id) { this.id = id; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
