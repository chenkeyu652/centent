package com.centent.core.exception;

public class HttpRequestException extends BusinessException {
    public static final int CODE = -5;
    public static final String MESSAGE = "接口请求异常...";

    public HttpRequestException() {
        super(MESSAGE);
    }

    public HttpRequestException(String message) {
        super(message);
    }

    public HttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpRequestException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
