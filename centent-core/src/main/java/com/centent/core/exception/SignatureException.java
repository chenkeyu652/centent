package com.centent.core.exception;

public class SignatureException extends BusinessException {
    public static final int CODE = -6;
    public static final String MESSAGE = "签名验证错误...";

    public SignatureException() {
        super(MESSAGE);
    }

    public SignatureException(String message) {
        super(message);
    }

    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
