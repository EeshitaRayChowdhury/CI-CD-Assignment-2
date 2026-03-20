package com.customerorders.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public OrderEntity() {}

    public OrderEntity(LocalDate orderDate, BigDecimal amount, Customer customer) {
        this.orderDate = orderDate;
        this.amount = amount;
        this.customer = customer;
    }

    public Long getId() { return id; }
    public LocalDate getOrderDate() { return orderDate; }
    public BigDecimal getAmount() { return amount; }
    public Customer getCustomer() { return customer; }

    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
