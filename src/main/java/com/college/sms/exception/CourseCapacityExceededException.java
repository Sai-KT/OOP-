package com.college.sms.exception;

public class CourseCapacityExceededException extends StudentManagementException {
    public CourseCapacityExceededException(String message) {
        super(message);
    }
}
