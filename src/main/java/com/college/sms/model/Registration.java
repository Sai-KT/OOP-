package com.college.sms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * ==============================================================================
 * Registration (OOP Concept: Association)
 * ==============================================================================
 * Represents a student's enrollment in a course.
 * Demonstrates:
 * 1. Association: Connects independent Student and Course entities via their keys.
 * 2. Immutable Identity: Establishes a link that can be verified and deregistered.
 * ==============================================================================
 */
public class Registration {

    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String studentId;
    private String studentName;
    private String courseCode;
    private String courseName;
    private String registrationDate;

    /**
     * Default constructor for dynamic initialization.
     */
    public Registration() {
        this.registrationDate = LocalDateTime.now().format(FORMATTER);
    }

    /**
     * Parameterized Constructor (Association Initialization).
     *
     * @param id          Unique enrollment record ID
     * @param studentId   Reference to student's studentId
     * @param studentName Cached snapshot of student's full name
     * @param courseCode  Reference to course's courseCode
     * @param courseName  Cached snapshot of course's title
     */
    public Registration(Long id, String studentId, String studentName, 
                        String courseCode, String courseName) {
        this.id = id;
        this.studentId = studentId != null ? studentId.trim() : "";
        this.studentName = studentName != null ? studentName.trim() : "";
        this.courseCode = courseCode != null ? courseCode.trim().toUpperCase() : "";
        this.courseName = courseName != null ? courseName.trim() : "";
        this.registrationDate = LocalDateTime.now().format(FORMATTER);
    }

    /**
     * Parameterized Constructor with explicit registration timestamp.
     */
    public Registration(Long id, String studentId, String studentName, 
                        String courseCode, String courseName, String registrationDate) {
        this(id, studentId, studentName, courseCode, courseName);
        if (registrationDate != null && !registrationDate.trim().isEmpty()) {
            this.registrationDate = registrationDate.trim();
        }
    }

    // --------------------------------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------------------------------

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
        this.studentId = studentId != null ? studentId.trim() : "";
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName != null ? studentName.trim() : "";
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode != null ? courseCode.trim().toUpperCase() : "";
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName != null ? courseName.trim() : "";
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    // --------------------------------------------------------------------------
    // Identity & Comparison
    // --------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return Objects.equals(studentId, that.studentId) &&
               Objects.equals(courseCode, that.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseCode);
    }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                '}';
    }
}
