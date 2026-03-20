package com.customerorders.service;

import com.customerorders.dto.CreateOrderRequest;
import com.customerorders.dto.OrderDTO;
import com.customerorders.dto.UpdateOrderRequest;
import com.customerorders.entity.Customer;
import com.customerorders.entity.OrderEntity;
import com.customerorders.exception.BadRequestException;
import com.customerorders.exception.ResourceNotFoundException;
import com.customerorders.repository.CustomerRepository;
import com.customerorders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getOrdersForCustomer_shouldReturnPagedOrders() {
        Customer customer = new Customer("John", "john@example.com");
        customer.setId(1L);

        OrderEntity order = new OrderEntity(
                LocalDate.of(2026, 3, 1),
                new BigDecimal("120.50"),
                customer
        );

        Page<OrderEntity> orderPage = new PageImpl<>(List.of(order));

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByCustomerId(eq(1L), any(Pageable.class))).thenReturn(orderPage);

        Page<OrderDTO> result = orderService.getOrdersForCustomer(1L, 0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals(new BigDecimal("120.50"), result.getContent().get(0).getAmount());

        verify(customerRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).findByCustomerId(eq(1L), any(Pageable.class));
    }

    @Test
    void getOrdersForCustomer_shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrdersForCustomer(1L, 0, 5)
        );

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void createOrderForCustomer_shouldReturnOrderDTO_whenOrderIsCreated() {
        Customer customer = new Customer("John", "john@example.com");
        customer.setId(1L);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderDate(LocalDate.of(2026, 3, 1));
        request.setAmount(new BigDecimal("150.00"));

        OrderEntity savedOrder = new OrderEntity(
                LocalDate.of(2026, 3, 1),
                new BigDecimal("150.00"),
                customer
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        OrderDTO result = orderService.createOrderForCustomer(1L, request);

        assertEquals(LocalDate.of(2026, 3, 1), result.getOrderDate());
        assertEquals(new BigDecimal("150.00"), result.getAmount());

        verify(customerRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void createOrderForCustomer_shouldThrowException_whenCustomerNotFound() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderDate(LocalDate.of(2026, 3, 1));
        request.setAmount(new BigDecimal("150.00"));

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.createOrderForCustomer(1L, request)
        );

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void updateOrderForCustomer_shouldReturnUpdatedOrder_whenValid() {
        Customer customer = new Customer("John", "john@example.com");
        customer.setId(1L);

        OrderEntity order = new OrderEntity(
                LocalDate.of(2026, 3, 1),
                new BigDecimal("100.00"),
                customer
        );

        UpdateOrderRequest request = new UpdateOrderRequest(
                LocalDate.of(2026, 3, 5),
                new BigDecimal("200.00")
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(order);

        OrderDTO result = orderService.updateOrderForCustomer(1L, 1L, request);

        assertEquals(LocalDate.of(2026, 3, 5), result.getOrderDate());
        assertEquals(new BigDecimal("200.00"), result.getAmount());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrderForCustomer_shouldThrowException_whenOrderNotFound() {
        UpdateOrderRequest request = new UpdateOrderRequest(
                LocalDate.of(2026, 3, 5),
                new BigDecimal("200.00")
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.updateOrderForCustomer(1L, 1L, request)
        );

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void updateOrderForCustomer_shouldThrowException_whenOrderBelongsToDifferentCustomer() {
        Customer customer = new Customer("John", "john@example.com");
        customer.setId(2L);

        OrderEntity order = new OrderEntity(
                LocalDate.of(2026, 3, 1),
                new BigDecimal("100.00"),
                customer
        );

        UpdateOrderRequest request = new UpdateOrderRequest(
                LocalDate.of(2026, 3, 5),
                new BigDecimal("200.00")
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.updateOrderForCustomer(1L, 1L, request)
        );

        assertEquals("Order not found for this customer", exception.getMessage());
    }

    @Test
    void getOrdersByDateRange_shouldReturnOrders() {
        Customer customer = new Customer("John", "john@example.com");
        customer.setId(1L);

        OrderEntity order = new OrderEntity(
                LocalDate.of(2026, 3, 1),
                new BigDecimal("120.50"),
                customer
        );

        Page<OrderEntity> orderPage = new PageImpl<>(List.of(order));

        when(orderRepository.findByOrderDateBetween(
                eq(LocalDate.of(2026, 3, 1)),
                eq(LocalDate.of(2026, 3, 31)),
                any(Pageable.class))
        ).thenReturn(orderPage);

        Page<OrderDTO> result = orderService.getOrdersByDateRange(
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31),
                0,
                5
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(new BigDecimal("120.50"), result.getContent().get(0).getAmount());
    }

    @Test
    void deleteOrder_shouldDeleteOrder_whenValidCustomerOwnsOrder() {
        Customer customer = new Customer("John", "john@example.com");
        customer.setId(1L);

        OrderEntity order = new OrderEntity(
                LocalDate.of(2026, 3, 1),
                new BigDecimal("100.00"),
                customer
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L, 1L);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void deleteOrder_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.deleteOrder(1L, 1L)
        );

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void deleteOrder_shouldThrowException_whenOrderBelongsToDifferentCustomer() {
        Customer customer = new Customer("John", "john@example.com");
        customer.setId(2L);

        OrderEntity order = new OrderEntity(
                LocalDate.of(2026, 3, 1),
                new BigDecimal("100.00"),
                customer
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.deleteOrder(1L, 1L)
        );

        assertEquals("Order not found for this customer", exception.getMessage());
    }
}