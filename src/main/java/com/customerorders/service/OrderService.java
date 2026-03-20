package com.customerorders.service;

import com.customerorders.dto.CreateOrderRequest;
import com.customerorders.dto.OrderDTO;
import com.customerorders.dto.UpdateOrderRequest;
import com.customerorders.entity.Customer;
import com.customerorders.entity.OrderEntity;
import com.customerorders.exception.BadRequestException;
import com.customerorders.exception.ResourceNotFoundException;
import com.customerorders.mapper.OrderMapper;
import com.customerorders.repository.CustomerRepository;
import com.customerorders.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public Page<OrderDTO> getOrdersForCustomer(Long customerId, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException("Invalid pagination parameters: page must be >= 0 and size must be > 0");
        }

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found");
        }

        return orderRepository.findByCustomerId(customerId, PageRequest.of(page, size))
                .map(OrderMapper::toDTO);
    }

    public OrderDTO createOrderForCustomer(Long customerId, CreateOrderRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        OrderEntity order = new OrderEntity(
                request.getOrderDate(),
                request.getAmount(),
                customer
        );

        OrderEntity saved = orderRepository.save(order);
        return OrderMapper.toDTO(saved);
    }

    public OrderDTO updateOrderForCustomer(Long customerId, Long orderId, UpdateOrderRequest request) {

        if (request.getOrderDate() == null) {
            throw new BadRequestException("orderDate is required");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("amount must be greater than 0");
        }

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Order not found for this customer");
        }

        order.setOrderDate(request.getOrderDate());
        order.setAmount(request.getAmount());

        OrderEntity updated = orderRepository.save(order);
        return OrderMapper.toDTO(updated);
    }

    public Page<OrderDTO> getOrdersByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException("Invalid pagination parameters: page must be >= 0 and size must be > 0");
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate cannot be after endDate");
        }

        return orderRepository.findByOrderDateBetween(startDate, endDate, PageRequest.of(page, size))
                .map(OrderMapper::toDTO);
    }

    public void deleteOrder(Long customerId, Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Order not found for this customer");
        }

        orderRepository.delete(order);
    }
}