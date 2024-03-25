package com.centent.core.exception;

public class CacheException extends BusinessException {
    public static final int CODE = -301;
    public static final String MESSAGE = "缓存操作异常...";

    public CacheException() {
        super(MESSAGE);
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
