package com.college.sms.model;

/**
 * ==============================================================================
 * UndergraduateStudent (OOP Concept: Inheritance & Method Overriding)
 * ==============================================================================
 * Concrete subclass extending the abstract Student base class.
 * Demonstrates:
 * 1. Single Inheritance: Inherits state and behavior from Student using 'extends'.
 * 2. Constructor Chaining: Reuses base initialization via 'super(...)'.
 * 3. Method Overriding: Polymorphically overrides getMaxCourses() and getStudentType().
 * ==============================================================================
 */
public class UndergraduateStudent extends Student {

    // Undergraduate students are permitted to take up to 6 courses per semester
    public static final int UG_MAX_COURSES = 6;

    /**
     * Default constructor for dynamic initialization.
     */
    public UndergraduateStudent() {
        super();
    }

    /**
     * Parameterized Constructor utilizing constructor chaining to the parent class.
     */
    public UndergraduateStudent(Long id, String studentId, String fullName, String email,
                                String phoneNumber, String department, int semester) {
        super(id, studentId, fullName, email, phoneNumber, department, semester);
    }

    /**
     * Overridden method demonstrating Runtime Polymorphism.
     * When RegistrationService calls student.getMaxCourses() on an UndergraduateStudent
     * instance, dynamic binding executes this method returning 6.
     */
    @Override
    public int getMaxCourses() {
        return UG_MAX_COURSES;
    }

    @Override
    public String getStudentType() {
        return "Undergraduate";
    }
}
