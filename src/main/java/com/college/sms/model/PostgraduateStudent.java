package com.college.sms.model;

/**
 * ==============================================================================
 * PostgraduateStudent (OOP Concept: Inheritance & Method Overriding)
 * ==============================================================================
 * Concrete subclass extending the abstract Student base class.
 * Demonstrates:
 * 1. Single Inheritance: Specializes Student for Master's/Ph.D. research curricula.
 * 2. Constructor Chaining: Reuses base initialization via 'super(...)'.
 * 3. Runtime Polymorphism: Overrides getMaxCourses() returning 4 (focused course load).
 * ==============================================================================
 */
public class PostgraduateStudent extends Student {

    // Postgraduate students are permitted to take up to 4 courses per semester
    public static final int PG_MAX_COURSES = 4;

    /**
     * Default constructor for dynamic initialization.
     */
    public PostgraduateStudent() {
        super();
    }

    /**
     * Parameterized Constructor utilizing constructor chaining to parent class.
     */
    public PostgraduateStudent(Long id, String studentId, String fullName, String email,
                               String phoneNumber, String department, int semester) {
        super(id, studentId, fullName, email, phoneNumber, department, semester);
    }

    /**
     * Overridden method demonstrating Runtime Polymorphism.
     * When RegistrationService calls student.getMaxCourses() on a PostgraduateStudent
     * instance, dynamic binding executes this method returning 4.
     */
    @Override
    public int getMaxCourses() {
        return PG_MAX_COURSES;
    }

    @Override
    public String getStudentType() {
        return "Postgraduate";
    }
}
