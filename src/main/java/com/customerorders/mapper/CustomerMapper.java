package com.customerorders.mapper;

import com.customerorders.dto.CustomerDTO;
import com.customerorders.entity.Customer;

public class CustomerMapper {

    public static CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getOrders().size()
        );
    }
}
