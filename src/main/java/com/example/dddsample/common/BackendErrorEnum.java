package com.example.dddsample.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BackendErrorEnum {

    ERROR_100001(100001, "Record not found"),
    ERROR_100010(100010, "Invalid parameter"),
    ERROR_101001(101001, "Order amount must be positive"),
    ERROR_101002(101002, "Order status transition not allowed"),
    ERROR_101003(101003, "User active order quota exceeded"),
    ERROR_101004(101004, "Payment charge failed");

    private final int code;
    private final String defaultMessage;
}
