package com.customerorders.mapper;

import com.customerorders.dto.OrderDTO;
import com.customerorders.entity.OrderEntity;

public class OrderMapper {

    public static OrderDTO toDTO(OrderEntity order) {
        return new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getAmount()
        );
    }
}
