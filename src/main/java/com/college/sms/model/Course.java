package com.college.sms.model;

import java.util.Objects;

/**
 * ==============================================================================
 * Course (OOP Concept: Encapsulation, Constructor Overloading & Static Members)
 * ==============================================================================
 * Represents an academic course offered by the university.
 * Demonstrates:
 * 1. Encapsulation: State is protected via private members and validated mutators.
 * 2. Constructor Overloading: Multiple constructors provide flexible initialization.
 * 3. Static Members: Shared default capacity constants and validation thresholds.
 * ==============================================================================
 */
public class Course {

    // Static Constants (Shared across all Course instances)
    public static final int DEFAULT_CAPACITY = 40;
    public static final int DEFAULT_CREDITS = 3;
    public static final int MAX_ALLOWABLE_CREDITS = 8;
    public static final int MAX_ALLOWABLE_CAPACITY = 200;

    private Long id;
    private String courseCode;   // Unique alphanumeric code (e.g., CS302)
    private String courseName;   // Official course title
    private int credits;         // Academic credits (1 to 8)
    private int maxCapacity;     // Maximum enrollment seating capacity

    /**
     * Default constructor for dynamic initialization.
     */
    public Course() {
        this.credits = DEFAULT_CREDITS;
        this.maxCapacity = DEFAULT_CAPACITY;
    }

    /**
     * Overloaded Constructor 1: Fully parameterized constructor.
     *
     * @param id          Unique database/storage ID
     * @param courseCode  Unique course code
     * @param courseName  Descriptive course title
     * @param credits     Credits awarded upon completion
     * @param maxCapacity Maximum seat capacity
     */
    public Course(Long id, String courseCode, String courseName, int credits, int maxCapacity) {
        this.id = id;
        setCourseCode(courseCode);
        setCourseName(courseName);
        setCredits(credits);
        setMaxCapacity(maxCapacity);
    }

    /**
     * Overloaded Constructor 2 (Demonstrates Constructor Overloading):
     * Accepts basic course properties and delegates to Constructor 1 using default capacity.
     */
    public Course(Long id, String courseCode, String courseName, int credits) {
        this(id, courseCode, courseName, credits, DEFAULT_CAPACITY);
    }

    // --------------------------------------------------------------------------
    // Encapsulated Getters and Setters with Validation
    // --------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty or null.");
        }
        this.courseCode = courseCode.trim().toUpperCase();
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty.");
        }
        this.courseName = courseName.trim();
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        if (credits <= 0 || credits > MAX_ALLOWABLE_CREDITS) {
            throw new IllegalArgumentException(
                    "Course credits must be between 1 and " + MAX_ALLOWABLE_CREDITS + ". Provided: " + credits);
        }
        this.credits = credits;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity <= 0 || maxCapacity > MAX_ALLOWABLE_CAPACITY) {
            throw new IllegalArgumentException(
                    "Course capacity must be between 1 and " + MAX_ALLOWABLE_CAPACITY + ". Provided: " + maxCapacity);
        }
        this.maxCapacity = maxCapacity;
    }

    // --------------------------------------------------------------------------
    // Identity & Comparison
    // --------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseCode, course.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}
