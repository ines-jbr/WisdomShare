package com.wisdomshare.demo.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;
import com.wisdomshare.demo.exception.operationnotpermittedexception;

@RestControllerAdvice
public class globalexceptionhandler {

        // ── Custom business exceptions ───────────────────────────────────────
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ExceptionResponse> handleBusinessException(
                        BusinessException ex,
                        HttpServletRequest request) {

                BusinessErrorCodes code = ex.getErrorCode();

                ExceptionResponse response = ExceptionResponse.builder()
                                .businessErrorCode(code.getCode())
                                .businessExceptionDescription(code.getDescription())
                                .error(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity
                                .status(code.getHttpStatus())
                                .body(response);
        }

        // ── Validation errors (@Valid / @Validated) ──────────────────────────
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ExceptionResponse> handleValidationErrors(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                String details = ex.getBindingResult().getFieldErrors().stream()
                                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                                .collect(Collectors.joining(", "));

                ExceptionResponse response = ExceptionResponse.builder()
                                .businessErrorCode(BusinessErrorCodes.INVALID_REQUEST.getCode())
                                .businessExceptionDescription(BusinessErrorCodes.INVALID_REQUEST.getDescription())
                                .error(details.isEmpty() ? "Validation failed" : details)
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // ── Security / Auth exceptions ───────────────────────────────────────
        @ExceptionHandler(LockedException.class)
        public ResponseEntity<ExceptionResponse> handleLockedException(
                        LockedException ex,
                        HttpServletRequest request) {

                ExceptionResponse response = ExceptionResponse.builder()
                                .businessErrorCode(BusinessErrorCodes.ACCOUNT_LOCKED.getCode())
                                .businessExceptionDescription(BusinessErrorCodes.ACCOUNT_LOCKED.getDescription())
                                .error(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(response);
        }

        // ── Catch-all ────────────────────────────────────────────────────────
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ExceptionResponse> handleGenericException(
                        Exception ex,
                        HttpServletRequest request) {

                ExceptionResponse response = ExceptionResponse.builder()
                                .businessErrorCode(BusinessErrorCodes.INTERNAL_ERROR.getCode())
                                .businessExceptionDescription(BusinessErrorCodes.INTERNAL_ERROR.getDescription())
                                .error(ex.getMessage() != null ? ex.getMessage() : "Unexpected error")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

}