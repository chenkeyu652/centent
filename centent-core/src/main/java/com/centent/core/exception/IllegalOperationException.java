package com.centent.core.exception;

public class IllegalOperationException extends BusinessException {

    public static final int CODE = -3;
    public static final String MESSAGE = "非法操作...";

    public IllegalOperationException() {
        super(MESSAGE);
    }

    public IllegalOperationException(String message) {
        super(message);
    }

    public IllegalOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalOperationException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
