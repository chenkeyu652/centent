package com.centent.core.exception;

public class NotFoundException extends BusinessException {
    public static final int CODE = 404;
    public static final String MESSAGE = "资源未找到...";

    public NotFoundException() {
        super(MESSAGE);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
