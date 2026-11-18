package com.example.dddsample.domain.order.repo;

import java.util.List;
import java.util.Optional;

import com.example.dddsample.domain.order.Order;

public interface OrderRepo {

    Long save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findByUserId(Long userId);

    void deleteById(Long id);
}
