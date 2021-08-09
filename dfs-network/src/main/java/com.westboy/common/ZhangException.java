package com.westboy.common;

public class ZhangException extends RuntimeException {
    private static final long serialVersionUID = -3466251773811872820L;

    public ZhangException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZhangException(String message) {
        super(message);
    }

    public ZhangException(Throwable cause) {
        super(cause);
    }

    public ZhangException() {
        super();
    }

}
