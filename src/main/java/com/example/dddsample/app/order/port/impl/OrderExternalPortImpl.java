package com.example.dddsample.app.order.port.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.dddsample.app.order.port.OrderExternalPort;
import com.example.dddsample.app.order.port.dto.OrderSummaryPortData;
import com.example.dddsample.app.order.query.OrderQueryService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderExternalPortImpl implements OrderExternalPort {

    private final OrderQueryService orderQueryService;

    @Override
    public Optional<OrderSummaryPortData> findOrderSummary(Long orderId) {
        return orderQueryService.getById(orderId)
            .map(r -> new OrderSummaryPortData(r.id(), r.userId(), r.amount(), r.status()));
    }
}
