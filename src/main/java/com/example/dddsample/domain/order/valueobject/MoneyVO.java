package com.example.dddsample.domain.order.valueobject;

import java.math.BigDecimal;

import com.example.dddsample.shared.BackendErrorEnum;
import com.example.dddsample.shared.BackendException;

public record MoneyVO(BigDecimal amount, String currency) {

    public MoneyVO {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BackendException(BackendErrorEnum.ERROR_101001);
        }
        if (currency == null || currency.isBlank()) {
            throw new BackendException(BackendErrorEnum.ERROR_100010);
        }
    }
}
