package com.example.dddsample.app.order.port.dto;

import java.math.BigDecimal;

public record OrderSummaryPortData(
    Long orderId,
    Long userId,
    BigDecimal amount,
    String status
) {
}
