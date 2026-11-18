package com.example.dddsample.app.order.command.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderCreateCommand(
    @NotBlank(message = "{100010}") String skuCode,
    @NotNull(message = "{100010}") @Min(value = 1, message = "{100010}") Integer quantity,
    @NotNull(message = "{101001}") @Positive(message = "{101001}") BigDecimal amount,
    @NotBlank(message = "{100010}") String currency
) {
}
