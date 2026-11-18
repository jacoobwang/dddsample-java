package com.example.dddsample.domain.order.validator;

import java.util.Optional;

import com.example.dddsample.shared.BackendErrorEnum;
import com.example.dddsample.shared.BackendException;
import com.example.dddsample.domain.order.Order;

public final class OrderQuotaValidator {

    private static final int MAX_ACTIVE_ORDERS_PER_USER = 10;

    private OrderQuotaValidator() {
    }

    public static void requireWithinQuota(long currentActiveCount) {
        if (currentActiveCount >= MAX_ACTIVE_ORDERS_PER_USER) {
            throw new BackendException(BackendErrorEnum.ERROR_101003);
        }
    }

    public static Order requirePresent(Optional<Order> orderOpt) {
        return orderOpt.orElseThrow(() -> new BackendException(BackendErrorEnum.ERROR_100001));
    }
}
