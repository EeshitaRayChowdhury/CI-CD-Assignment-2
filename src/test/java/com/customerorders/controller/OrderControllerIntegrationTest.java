package com.customerorders.controller;

import com.customerorders.dto.CreateOrderRequest;
import com.customerorders.entity.Customer;
import com.customerorders.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        customer = customerRepository.save(
                new Customer("John Doe", "john@example.com")
        );
    }

    @Test
    void createOrder_shouldReturn201_whenValidRequest() throws Exception {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setAmount(BigDecimal.valueOf(200));
        request.setOrderDate(LocalDate.now());

        mockMvc.perform(post("/customers/{customerId}/orders", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(200));
    }

    @Test
    void createOrder_shouldReturn404_whenCustomerNotFound() throws Exception {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setOrderDate(LocalDate.now());

        mockMvc.perform(post("/customers/{customerId}/orders", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}