package com.example.dddsample.app.order.port;

import java.util.Optional;

import com.example.dddsample.app.order.port.dto.OrderSummaryPortData;

public interface OrderExternalPort {

    Optional<OrderSummaryPortData> findOrderSummary(Long orderId);
}
