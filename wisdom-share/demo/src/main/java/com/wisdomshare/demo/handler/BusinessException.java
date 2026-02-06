package com.wisdomshare.demo.handler;  // ← adjust to your actual package

import lombok.Getter;

/**
 * Custom business-level exception for the application.
 * Always carries a BusinessErrorCodes (even when a simple message is provided).
 */
@Getter
public class BusinessException extends RuntimeException {

    private final BusinessErrorCodes errorCode;

    /**
     * Constructor with error code only (uses the description from the code)
     */
    public BusinessException(BusinessErrorCodes errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error code + custom message
     */
    public BusinessException(BusinessErrorCodes errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error code + cause (for wrapping other exceptions)
     */
    public BusinessException(BusinessErrorCodes errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error code + custom message + cause
     */
    public BusinessException(BusinessErrorCodes errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
    }

    /**
     * Convenience constructor when you only have a message
     * → uses a fallback/generic error code
     */
    public BusinessException(String message) {
        this(BusinessErrorCodes.GENERIC_BUSINESS_ERROR, message);
    }

    /**
     * Convenience constructor when you only have message + cause
     * → uses a fallback/generic error code
     */
    public BusinessException(String message, Throwable cause) {
        this(BusinessErrorCodes.GENERIC_BUSINESS_ERROR, message, cause);
    }

    // Optional: if you want to expose a shortcut method
    public String getErrorDescription() {
        return errorCode != null ? errorCode.getDescription() : getMessage();
    }
}