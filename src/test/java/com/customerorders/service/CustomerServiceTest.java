package com.customerorders.service;

import com.customerorders.dto.CreateCustomerRequest;
import com.customerorders.dto.CustomerDTO;
import com.customerorders.entity.Customer;
import com.customerorders.exception.BadRequestException;
import com.customerorders.exception.ResourceNotFoundException;
import com.customerorders.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getAllCustomers_shouldReturnPagedCustomers() {
        Customer customer = new Customer("John Doe", "john@example.com");

        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        when(customerRepository.findAll(any(Pageable.class))).thenReturn(customerPage);

        Page<CustomerDTO> result = customerService.getAllCustomers(0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getName());
        assertEquals("john@example.com", result.getContent().get(0).getEmail());

        verify(customerRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllCustomers_shouldThrowException_whenPaginationIsInvalid() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> customerService.getAllCustomers(-1, 0)
        );

        assertEquals("Invalid pagination parameters: page must be >= 0 and size must be > 0", exception.getMessage());
    }

    @Test
    void getCustomerById_shouldReturnCustomer_whenCustomerExists() {
        Customer customer = new Customer("Alice", "alice@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());

        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(1L)
        );

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void createCustomer_shouldReturnCustomerDTO_whenCustomerIsCreated() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("Bob");
        request.setEmail("bob@example.com");

        Customer savedCustomer = new Customer("Bob", "bob@example.com");

        when(customerRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerDTO result = customerService.createCustomer(request);

        assertEquals("Bob", result.getName());
        assertEquals("bob@example.com", result.getEmail());

        verify(customerRepository, times(1)).existsByEmail("bob@example.com");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_shouldThrowException_whenEmailAlreadyExists() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("Bob");
        request.setEmail("bob@example.com");

        when(customerRepository.existsByEmail("bob@example.com")).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> customerService.createCustomer(request)
        );

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void deleteCustomer_shouldDeleteCustomer_whenCustomerExists() {
        Customer customer = new Customer("John", "john@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(1L);

        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    void deleteCustomer_shouldThrowException_whenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.deleteCustomer(1L)
        );

        assertEquals("Customer not found", exception.getMessage());
    }
}