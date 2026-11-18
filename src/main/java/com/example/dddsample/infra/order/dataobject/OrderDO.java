package com.example.dddsample.infra.order.dataobject;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String skuCode;
    private Integer quantity;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentTransactionId;
    private String trackingNumber;
    private Long createdAt;
}
