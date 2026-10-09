package com.college.sms.exception;

/**
 * ==============================================================================
 * StudentManagementException (OOP Concept: Exception Hierarchy Root)
 * ==============================================================================
 * Base runtime exception for all business and domain errors in the system.
 * Demonstrates:
 * 1. User-defined exception hierarchies.
 * 2. Exception propagation without forcing checked exception boilerplates on callers.
 * ==============================================================================
 */
public class StudentManagementException extends RuntimeException {

    public StudentManagementException(String message) {
        super(message);
    }

    public StudentManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
