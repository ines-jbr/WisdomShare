package com.wisdomshare.demo.handler;  // ← adjust to your real package

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCodes {

    // ────────────────────────────────────────────────
    // Authentication / Security
    // ────────────────────────────────────────────────
    INVALID_CREDENTIALS     (1001, "Invalid username or password",       HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED          (1002, "Account is locked",                   HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED           (1003, "Authentication token has expired",    HttpStatus.UNAUTHORIZED),
     INVALID_REQUEST(2001, "Invalid request parameters", HttpStatus.BAD_REQUEST),
    // ────────────────────────────────────────────────
    // Book / ISBN related
    // ────────────────────────────────────────────────
    ISBN_ALREADY_EXISTS     (2001, "A book with this ISBN already exists", HttpStatus.CONFLICT),
    BOOK_NOT_FOUND          (2002, "Book not found",                       HttpStatus.NOT_FOUND),
    BOOK_UPDATE_NOT_ALLOWED (2003, "You are not allowed to update this book", HttpStatus.FORBIDDEN),

    // ────────────────────────────────────────────────
    // Generic / fallback
    // ────────────────────────────────────────────────
    GENERIC_BUSINESS_ERROR  (9998, "An unexpected business error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_ERROR          (9999, "Internal server error",                 HttpStatus.INTERNAL_SERVER_ERROR);

    // ────────────────────────────────────────────────
    // Fields + constructor
    // ────────────────────────────────────────────────
    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, String description, HttpStatus httpStatus) {
        this.code        = code;
        this.description = description;
        this.httpStatus  = httpStatus;
    }
}