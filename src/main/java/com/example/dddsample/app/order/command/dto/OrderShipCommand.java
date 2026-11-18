package com.example.dddsample.app.order.command.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderShipCommand(
    @NotBlank(message = "{100010}") String trackingNumber
) {
}
