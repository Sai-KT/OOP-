package com.college.sms.dto;

import com.college.sms.model.Student;

/**
 * Outgoing HTTP response for Student entities.
 */
public class StudentResponse {

    private Long id;
    private String studentId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String department;
    private int semester;
    private String studentType;
    private int maxCourses;

    public StudentResponse() {}

    public static StudentResponse fromEntity(Student s) {
        StudentResponse dto = new StudentResponse();
        dto.setId(s.getId());
        dto.setStudentId(s.getStudentId());
        dto.setFullName(s.getFullName());
        dto.setEmail(s.getEmail());
        dto.setPhoneNumber(s.getPhoneNumber());
        dto.setDepartment(s.getDepartment());
        dto.setSemester(s.getSemester());
        dto.setStudentType(s.getStudentType());
        dto.setMaxCourses(s.getMaxCourses());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getMaxCourses() {
        return maxCourses;
    }

    public void setMaxCourses(int maxCourses) {
        this.maxCourses = maxCourses;
    }
}
