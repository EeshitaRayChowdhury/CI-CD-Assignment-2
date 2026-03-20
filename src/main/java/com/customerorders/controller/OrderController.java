package com.customerorders.controller;

import com.customerorders.dto.CreateOrderRequest;
import com.customerorders.dto.OrderDTO;
import com.customerorders.dto.UpdateOrderRequest;
import com.customerorders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<Page<OrderDTO>> getOrdersForCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<OrderDTO> orders = orderService.getOrdersForCustomer(customerId, page, size);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/customers/{customerId}/orders")
    public ResponseEntity<OrderDTO> createOrderForCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderDTO createdOrder = orderService.createOrderForCustomer(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/customers/{customerId}/orders/{orderId}")
    public ResponseEntity<OrderDTO> updateOrderForCustomer(
            @PathVariable Long customerId,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderRequest request
    ) {
        OrderDTO updatedOrder = orderService.updateOrderForCustomer(customerId, orderId, request);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/customers/{customerId}/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long customerId,
            @PathVariable Long orderId
    ) {
        orderService.deleteOrder(customerId, orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<OrderDTO> orders = orderService.getOrdersByDateRange(startDate, endDate, page, size);
        return ResponseEntity.ok(orders);
    }
}