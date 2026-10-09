package com.college.sms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Incoming HTTP request body for creating or updating a course.
 */
public class CourseRequest {

    @NotBlank(message = "Course Code is required.")
    private String courseCode;

    @NotBlank(message = "Course Name is required.")
    private String courseName;

    @Min(value = 1, message = "Credits must be at least 1.")
    @Max(value = 8, message = "Credits cannot exceed 8.")
    private int credits;

    @Min(value = 1, message = "Maximum capacity must be at least 1.")
    @Max(value = 200, message = "Maximum capacity cannot exceed 200.")
    private int maxCapacity;

    public CourseRequest() {}

    public CourseRequest(String courseCode, String courseName, int credits, int maxCapacity) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.maxCapacity = maxCapacity;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
