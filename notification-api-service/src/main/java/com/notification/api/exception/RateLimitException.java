package com.notification.api.exception;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String msg) {
        super(msg);
    }
}
