package com.example.dddsample.app.order.command;

import com.example.dddsample.app.order.command.dto.OrderCreateCommand;
import com.example.dddsample.app.order.command.dto.OrderShipCommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface OrderCommandService {

    Long create(@NotNull(message = "{100010}") Long userId, @NotNull(message = "{100010}") @Valid OrderCreateCommand command);

    String pay(@NotNull(message = "{100010}") Long orderId, @NotNull(message = "{100010}") Long userId);

    void ship(@NotNull(message = "{100010}") Long orderId, @NotNull(message = "{100010}") @Valid OrderShipCommand command);

    void deleteById(@NotNull(message = "{100010}") Long orderId);
}
