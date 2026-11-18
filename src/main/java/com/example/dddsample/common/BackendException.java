package com.example.dddsample.common;

import lombok.Getter;

@Getter
public class BackendException extends RuntimeException {

    private final BackendErrorEnum errorEnum;

    public BackendException(BackendErrorEnum errorEnum) {
        super(errorEnum.getDefaultMessage());
        this.errorEnum = errorEnum;
    }
}
