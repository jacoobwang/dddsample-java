package com.example.dddsample.domain.order.policy.dto;

import java.math.BigDecimal;

public record ChargePolicy(
    Long userId,
    BigDecimal amount,
    String currency
) {
}
