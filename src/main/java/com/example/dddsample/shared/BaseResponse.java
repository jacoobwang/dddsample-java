package com.example.dddsample.shared;

public record BaseResponse<T>(int code, String message, T data) {

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, "OK", data);
    }

    public static BaseResponse<Void> success() {
        return new BaseResponse<>(0, "OK", null);
    }
}
