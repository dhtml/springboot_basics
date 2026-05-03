package com.tutorial.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * GLOBAL EXCEPTION HANDLER
 * ============================================================
 *
 * @RestControllerAdvice intercepts exceptions from ALL controllers.
 *                       It is a centralized place to handle errors —
 *                       controllers
 *                       stay clean and do NOT need try-catch blocks.
 *
 *                       WITHOUT this class: Spring returns ugly HTML error
 *                       pages or
 *                       raw stack traces. Clients see confusing, inconsistent
 *                       errors.
 *
 *                       WITH this class: Every error is caught and formatted as
 *                       a
 *                       consistent, descriptive JSON response. Example:
 *                       {
 *                       "status": 404,
 *                       "error": "Resource Not Found",
 *                       "message": "Student not found with id : '999'",
 *                       "timestamp": "2024-10-15T10:30:00"
 *                       }
 *
 *                       HOW IT WORKS:
 * @ExceptionHandler(SomeException.class) tells Spring:
 *                                        "When SomeException is thrown anywhere
 *                                        in any controller,
 *                                        call this method to handle it."
 *
 *                                        Spring picks the MOST SPECIFIC
 *                                        handler. So:
 *                                        ResourceNotFoundException →
 *                                        handleResourceNotFoundException()
 *                                        MethodArgumentNotValidException →
 *                                        handleValidationException()
 *                                        Any other exception →
 *                                        handleGenericException() (catch-all)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * HANDLER 1: ResourceNotFoundException (HTTP 404)
     *
     * Triggered when: a student, product, etc. is not found by ID.
     *
     * RESPONSE EXAMPLE:
     * {
     * "status": 404,
     * "error": "Resource Not Found",
     * "message": "Student not found with id : '999'",
     * "timestamp": "2024-10-15T10:30:00"
     * }
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.NOT_FOUND.value()); // 404
        error.put("error", "Resource Not Found");
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * HANDLER 2: Validation Errors (HTTP 400)
     *
     * Triggered when: @Valid on a @RequestBody fails.
     * For example: @NotBlank fails, @Email format is wrong, @Size exceeded.
     *
     * This handler collects ALL field errors and returns them together,
     * so the client can fix everything in one go.
     *
     * RESPONSE EXAMPLE:
     * {
     * "status": 400,
     * "error": "Validation Failed",
     * "message": "One or more fields failed validation. See 'fieldErrors'.",
     * "fieldErrors": {
     * "email": "Please provide a valid email address",
     * "name": "Name is required"
     * },
     * "timestamp": "2024-10-15T10:30:00"
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Extract all field-level validation errors into a map: fieldName →
        // errorMessage
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            String fieldName = ((FieldError) objectError).getField();
            String errorMessage = objectError.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.BAD_REQUEST.value()); // 400
        error.put("error", "Validation Failed");
        error.put("message", "One or more fields failed validation. See 'fieldErrors'.");
        error.put("fieldErrors", fieldErrors);
        error.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * HANDLER 3: Business Rule Conflicts (HTTP 409)
     *
     * Triggered when: trying to create a student with a duplicate email.
     * We throw IllegalArgumentException from the service layer for this.
     *
     * RESPONSE EXAMPLE:
     * {
     * "status": 409,
     * "error": "Conflict",
     * "message": "A student with email 'alice@example.com' already exists.",
     * "timestamp": "..."
     * }
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            IllegalArgumentException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.CONFLICT.value()); // 409
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * HANDLER 4: No Endpoint Found (HTTP 404)
     *
     * Triggered when the client requests a URL that does not exist.
     * Spring 6 / Spring Boot 3 raises NoResourceFoundException for this.
     * Without this handler the catch-all below would return a misleading 500.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(
            NoResourceFoundException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", "No endpoint found for path: /" + ex.getResourcePath());
        error.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * HANDLER 5: Catch-All (HTTP 500)
     *
     * This is a safety net for ANY exception not handled above.
     * It prevents raw stack traces from reaching the client.
     *
     * IMPORTANT: In production, you would LOG the full exception here
     * (using a Logger) for debugging, but NEVER expose the stack trace
     * to the client — it reveals internal implementation details
     * and is a security risk.
     *
     * RESPONSE EXAMPLE:
     * {
     * "status": 500,
     * "error": "Internal Server Error",
     * "message": "An unexpected error occurred. Please try again later.",
     * "timestamp": "..."
     * }
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // TODO in production: logger.error("Unexpected error occurred", ex);

        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred. Please try again later.");
        error.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
