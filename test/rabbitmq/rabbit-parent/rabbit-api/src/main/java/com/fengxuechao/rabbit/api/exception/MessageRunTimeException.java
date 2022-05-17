package com.fengxuechao.rabbit.api.exception;

/**
 * $MessageRunTimeException
 *
 * @author fengxuechao
 */
public class MessageRunTimeException extends RuntimeException {

    private static final long serialVersionUID = 8651828913888663267L;

    public MessageRunTimeException() {
        super();
    }

    public MessageRunTimeException(String message) {
        super(message);
    }

    public MessageRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageRunTimeException(Throwable cause) {
        super(cause);
    }
}
