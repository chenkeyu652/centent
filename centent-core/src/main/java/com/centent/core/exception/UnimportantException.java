package com.centent.core.exception;

/**
 * 依据业务需要抛出异常，但是此异常不重要
 *
 * @since 0.0.1
 */
public class UnimportantException extends BusinessException {
    public static final int CODE = -7;

    public static final String MESSAGE = "不重要异常...";

    public UnimportantException() {
        super(MESSAGE);
    }

    public UnimportantException(String message) {
        super(message);
    }

    public UnimportantException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnimportantException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
