package com.customerorders.service;

import com.customerorders.dto.CreateCustomerRequest;
import com.customerorders.dto.CustomerDTO;
import com.customerorders.entity.Customer;
import com.customerorders.exception.BadRequestException;
import com.customerorders.exception.ResourceNotFoundException;
import com.customerorders.mapper.CustomerMapper;
import com.customerorders.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Page<CustomerDTO> getAllCustomers(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException("Invalid pagination parameters: page must be >= 0 and size must be > 0");
        }

        Pageable pageable = PageRequest.of(page, size);

        return customerRepository.findAll(pageable)
                .map(CustomerMapper::toDTO);
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return CustomerMapper.toDTO(customer);
    }

    public CustomerDTO createCustomer(CreateCustomerRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        Customer customer = new Customer(
                request.getName(),
                request.getEmail()
        );

        Customer saved = customerRepository.save(customer);

        return CustomerMapper.toDTO(saved);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customerRepository.delete(customer);
    }
}