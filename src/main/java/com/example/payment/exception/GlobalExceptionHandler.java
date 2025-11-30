package com.example.payment.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(InvalidSignatureException.class)
    public ResponseEntity<?> handleInvalidSignatureException(InvalidSignatureException ex) {
        log.warn("Invalid Signature: {}", ex.getMessage());
        return ResponseEntity.status(401).body(new ErrorResponse("INVALID_SIGNATURE", ex.getMessage()));
    }

    // generic error response container
    public static class ErrorResponse {
        public final String code;
        public final String message;
        public ErrorResponse(String code, String message) {
            this.code = code; this.message = message;
        }
    }
}