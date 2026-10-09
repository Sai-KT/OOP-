package com.college.sms.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Incoming HTTP request body for enrolling a student in a course.
 */
public class RegistrationRequest {

    @NotBlank(message = "Student ID is required.")
    private String studentId;

    @NotBlank(message = "Course Code is required.")
    private String courseCode;

    public RegistrationRequest() {}

    public RegistrationRequest(String studentId, String courseCode) {
        this.studentId = studentId;
        this.courseCode = courseCode;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
