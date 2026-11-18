package com.example.dddsample.domain.order.policy.dto;

public record ChargePolicyResult(
    boolean success,
    String transactionId
) {
}
