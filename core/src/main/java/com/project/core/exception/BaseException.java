package com.project.core.exception;

public class BaseException extends RuntimeException {
    private final String errorCode;

    public BaseException(String message) {
        super(message);
        this.errorCode = "GENERIC_ERROR";
    }

    public BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERIC_ERROR";
    }

    public BaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
