package com.college.sms.dto;

import jakarta.validation.constraints.*;

/**
 * Incoming HTTP request body for creating or updating a student.
 */
public class StudentRequest {

    @NotBlank(message = "Student ID is required.")
    private String studentId;

    @NotBlank(message = "Full Name is required.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Department is required.")
    private String department;

    @Min(value = 1, message = "Semester must be at least 1.")
    @Max(value = 12, message = "Semester must not exceed 12.")
    private int semester;

    @NotBlank(message = "Student Type is required (Undergraduate or Postgraduate).")
    private String studentType;

    public StudentRequest() {}

    public StudentRequest(String studentId, String fullName, String email, 
                          String phoneNumber, String department, int semester, String studentType) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.semester = semester;
        this.studentType = studentType;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }
}
