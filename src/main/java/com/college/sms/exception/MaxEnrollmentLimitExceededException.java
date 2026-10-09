package com.college.sms.exception;

public class MaxEnrollmentLimitExceededException extends StudentManagementException {
    public MaxEnrollmentLimitExceededException(String message) {
        super(message);
    }
}
