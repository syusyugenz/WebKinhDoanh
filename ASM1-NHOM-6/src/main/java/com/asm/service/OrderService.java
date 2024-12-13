package com.asm.service;

import com.asm.model.Order;
import com.asm.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> findTopOrders(Pageable pageable) {
        return orderRepository.findTopOrders(pageable);
    }

    // Other methods...
}
