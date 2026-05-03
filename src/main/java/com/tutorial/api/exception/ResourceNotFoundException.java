package com.tutorial.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ============================================================
 * CUSTOM EXCEPTION — Resource Not Found (HTTP 404)
 * ============================================================
 *
 * Custom exceptions allow you to represent specific error
 * scenarios with meaningful names and messages.
 *
 * WHY EXTEND RuntimeException?
 *   RuntimeException = "unchecked exception".
 *   You do NOT need to declare it with "throws" in method signatures.
 *   This keeps code cleaner for expected-but-rare failures (like 404s).
 *
 * @ResponseStatus(HttpStatus.NOT_FOUND)
 *   When this exception is thrown and NOT caught by a @ExceptionHandler,
 *   Spring automatically responds with HTTP 404 Not Found.
 *   Our GlobalExceptionHandler catches it first for a nicer JSON response.
 *
 * USAGE EXAMPLE:
 *   throw new ResourceNotFoundException("Student", "id", 42);
 *   → produces message: "Student not found with id : '42'"
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;  // e.g., "Student"
    private final String fieldName;     // e.g., "id"
    private final Object fieldValue;    // e.g., 42

    /**
     * @param resourceName the type of resource that was not found (e.g., "Student")
     * @param fieldName    the field used to look it up (e.g., "id", "email")
     * @param fieldValue   the value that produced no result (e.g., 42, "alice@example.com")
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(resourceName + " not found with " + fieldName + " : '" + fieldValue + "'");
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() { return resourceName; }
    public String getFieldName() { return fieldName; }
    public Object getFieldValue() { return fieldValue; }
}
