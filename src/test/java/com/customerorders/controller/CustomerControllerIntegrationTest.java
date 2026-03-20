package com.customerorders.controller;

import com.customerorders.dto.CreateCustomerRequest;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void getAllCustomers_shouldReturnPagedCustomers() throws Exception {
        customerRepository.save(new Customer("John Doe", "john@example.com"));
        customerRepository.save(new Customer("Alice Smith", "alice@example.com"));

        mockMvc.perform(get("/customers")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.content[1].name").value("Alice Smith"));
    }

    @Test
    void getCustomerById_shouldReturnCustomer_whenCustomerExists() throws Exception {
        Customer savedCustomer = customerRepository.save(new Customer("Bob", "bob@example.com"));

        mockMvc.perform(get("/customers/{id}", savedCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    void getCustomerById_shouldReturn404_whenCustomerDoesNotExist() throws Exception {
        mockMvc.perform(get("/customers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void createCustomer_shouldReturn201_whenValidRequest() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("Charlie");
        request.setEmail("charlie@example.com");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Charlie"))
                .andExpect(jsonPath("$.email").value("charlie@example.com"));
    }

    @Test
    void createCustomer_shouldReturn400_whenRequestIsInvalid() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("");
        request.setEmail("invalid-email");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void deleteCustomer_shouldReturn204_whenCustomerExists() throws Exception {
        Customer savedCustomer = customerRepository.save(new Customer("David", "david@example.com"));

        mockMvc.perform(delete("/customers/{id}", savedCustomer.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCustomer_shouldReturn404_whenCustomerDoesNotExist() throws Exception {
        mockMvc.perform(delete("/customers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}