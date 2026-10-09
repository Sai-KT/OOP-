package com.college.sms.exception;

public class DuplicateStudentException extends StudentManagementException {
    public DuplicateStudentException(String message) {
        super(message);
    }
}
