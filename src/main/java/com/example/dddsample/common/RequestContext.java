package com.example.dddsample.common;

public final class RequestContext {

    private static final ThreadLocal<Long> CURRENT_USER_ID = ThreadLocal.withInitial(() -> 10001L);

    private RequestContext() {
    }

    public static Long getUserIdRequired() {
        Long userId = CURRENT_USER_ID.get();
        if (userId == null) {
            throw new BackendException(BackendErrorEnum.ERROR_100010);
        }
        return userId;
    }

    public static void setUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static void clear() {
        CURRENT_USER_ID.remove();
    }
}
