package com.example.dddsample.app.order.query.dto;

import java.math.BigDecimal;

public record OrderDetailQueryResult(
    Long id,
    Long userId,
    String skuCode,
    Integer quantity,
    BigDecimal amount,
    String currency,
    String status,
    String paymentTransactionId,
    String trackingNumber,
    Long createdAt
) {
}
