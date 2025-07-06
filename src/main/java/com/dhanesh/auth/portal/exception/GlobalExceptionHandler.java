package com.dhanesh.auth.portal.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler to catch and convert custom and general exceptions
 * into standard HTTP responses with a consistent JSON format.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Utility method to build a structured error response.
     *
     * @param status  the HTTP status code
     * @param message the error message
     * @return a ResponseEntity containing a JSON error body
     */
    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Handles duplicate email registration attempts.
     *
     * @param ex the EmailAlreadyInUseException
     * @return a 409 Conflict error response
     */
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<Object> handleEmailAlreadyInUse(EmailAlreadyInUseException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles duplicate username registration attempts.
     *
     * @param ex the UsernameAlreadyTakenException
     * @return a 409 Conflict error response
     */
    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<Object> handleUsernameAlreadyTaken(UsernameAlreadyTakenException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles authentication failures (invalid credentials).
     *
     * @param ex the AuthenticationFailedException
     * @return a 409 Conflict error response
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Object> handleAuthenticationFailure(AuthenticationFailedException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles unverified email login attempts by including the email in the response.
     *
     * @param ex the EmailNotVerifiedException
     * @return a 401 Unauthorized error response
     */
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Object> handleEmailNotVerified(EmailNotVerifiedException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("email", ex.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Handles all uncaught exceptions (fallback).
     *
     * @param ex any exception not explicitly handled
     * @return a 500 Internal Server Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    }
}
