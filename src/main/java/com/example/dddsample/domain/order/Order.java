package com.example.dddsample.domain.order;

import java.time.Instant;

import com.example.dddsample.common.BackendErrorEnum;
import com.example.dddsample.common.BackendException;
import com.example.dddsample.domain.order.valueobject.MoneyVO;
import com.example.dddsample.shared.order.OrderStatusEnum;

public final class Order {

    private final Long id;
    private final Long userId;
    private final String skuCode;
    private final Integer quantity;
    private final MoneyVO totalPrice;
    private OrderStatusEnum status;
    private String paymentTransactionId;
    private String trackingNumber;
    private final Long createdAt;

    private Order(
            Long id,
            Long userId,
            String skuCode,
            Integer quantity,
            MoneyVO totalPrice,
            OrderStatusEnum status,
            String paymentTransactionId,
            String trackingNumber,
            Long createdAt) {
        if (userId == null || skuCode == null || skuCode.isBlank() || quantity == null || quantity <= 0 || totalPrice == null) {
            throw new BackendException(BackendErrorEnum.ERROR_100010);
        }
        this.id = id;
        this.userId = userId;
        this.skuCode = skuCode;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentTransactionId = paymentTransactionId;
        this.trackingNumber = trackingNumber;
        this.createdAt = createdAt;
    }

    public static Order create(Long userId, String skuCode, Integer quantity, MoneyVO totalPrice) {
        return new Order(
            null,
            userId,
            skuCode,
            quantity,
            totalPrice,
            OrderStatusEnum.CREATED,
            null,
            null,
            Instant.now().getEpochSecond()
        );
    }

    public static Order restore(
            Long id,
            Long userId,
            String skuCode,
            Integer quantity,
            MoneyVO totalPrice,
            OrderStatusEnum status,
            String paymentTransactionId,
            String trackingNumber,
            Long createdAt) {
        return new Order(id, userId, skuCode, quantity, totalPrice, status, paymentTransactionId, trackingNumber, createdAt);
    }

    public void markPaid(String transactionId) {
        if (this.status != OrderStatusEnum.CREATED) {
            throw new BackendException(BackendErrorEnum.ERROR_101002);
        }
        if (transactionId == null || transactionId.isBlank()) {
            throw new BackendException(BackendErrorEnum.ERROR_101004);
        }
        this.status = OrderStatusEnum.PAID;
        this.paymentTransactionId = transactionId;
    }

    public void ship(String trackingNumber) {
        if (this.status != OrderStatusEnum.PAID) {
            throw new BackendException(BackendErrorEnum.ERROR_101002);
        }
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new BackendException(BackendErrorEnum.ERROR_100010);
        }
        this.status = OrderStatusEnum.SHIPPED;
        this.trackingNumber = trackingNumber;
    }

    public Long id() {
        return id;
    }

    public Long userId() {
        return userId;
    }

    public String skuCode() {
        return skuCode;
    }

    public Integer quantity() {
        return quantity;
    }

    public MoneyVO totalPrice() {
        return totalPrice;
    }

    public OrderStatusEnum status() {
        return status;
    }

    public String paymentTransactionId() {
        return paymentTransactionId;
    }

    public String trackingNumber() {
        return trackingNumber;
    }

    public Long createdAt() {
        return createdAt;
    }
}
