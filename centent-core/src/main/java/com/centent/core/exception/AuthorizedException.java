package com.centent.core.exception;

public class AuthorizedException extends BusinessException {
    public static final int CODE = -2;
    public static final String MESSAGE = "用户未登录...";

    public AuthorizedException() {
        super(MESSAGE);
    }

    public AuthorizedException(String message) {
        super(message);
    }

    public AuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizedException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
