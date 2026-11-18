package com.example.dddsample.adapter.order.dto;

import java.math.BigDecimal;

public record OrderDisplayResponse(
    Long id,
    Long userId,
    String skuCode,
    Integer quantity,
    BigDecimal amount,
    String currency,
    String status,
    String statusLabel,
    String paymentTransactionId,
    String trackingNumber,
    Long createdAt
) {
}
