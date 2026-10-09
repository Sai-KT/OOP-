package com.college.sms.model;

import java.util.Objects;

/**
 * ==============================================================================
 * Student (OOP Concept: Abstract Class, Data Abstraction & Inheritance Root)
 * ==============================================================================
 * Abstract base class representing common properties and behaviors of all students.
 * Demonstrates:
 * 1. Abstraction: Declares abstract methods (getMaxCourses, getStudentType) whose
 *    specific implementations are deferred to concrete subclasses.
 * 2. Encapsulation: All data members are kept private with validated mutator access.
 * 3. Composition: Possesses a ContactDetails object for email and phone.
 * 4. Polymorphic Base: Provides the contract for runtime polymorphic dispatch.
 * ==============================================================================
 */
public abstract class Student {

    private Long id;
    private String studentId;     // Roll / Academic ID (e.g. STU101)
    private String fullName;      // Student's full name
    private String department;    // Academic department (e.g. Computer Science)
    private int semester;         // Current semester (1 to 8)
    private ContactDetails contactDetails; // Composition relationship

    /**
     * Default constructor for dynamic initialization and reflection.
     */
    public Student() {
        this.contactDetails = new ContactDetails();
    }

    /**
     * Parameterized Constructor (Invoked by derived classes via super()).
     *
     * @param id          Internal surrogate ID
     * @param studentId   Unique institutional student roll number
     * @param fullName    Student full name
     * @param email       Valid email address
     * @param phoneNumber Contact telephone number
     * @param department  Academic department
     * @param semester    Current semester (1 to 8)
     */
    public Student(Long id, String studentId, String fullName, String email, 
                   String phoneNumber, String department, int semester) {
        this.id = id;
        setStudentId(studentId);
        setFullName(fullName);
        this.contactDetails = new ContactDetails(email, phoneNumber);
        setDepartment(department);
        setSemester(semester);
    }

    // --------------------------------------------------------------------------
    // Abstract Methods (Subclasses must provide concrete implementations)
    // --------------------------------------------------------------------------

    /**
     * Runtime Polymorphism Hook:
     * Returns the maximum course enrollment limit based on the concrete student type.
     * Undergraduate: 6 courses
     * Postgraduate:  4 courses
     *
     * @return Maximum allowable registered courses
     */
    public abstract int getMaxCourses();

    /**
     * Identifies the student type category ("Undergraduate" or "Postgraduate").
     *
     * @return String literal representing student type
     */
    public abstract String getStudentType();

    // --------------------------------------------------------------------------
    // Encapsulated Getters and Setters with Validation
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
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty or null.");
        }
        this.studentId = studentId.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty.");
        }
        this.department = department.trim();
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        if (semester < 1 || semester > 12) {
            throw new IllegalArgumentException("Semester must be between 1 and 12. Provided: " + semester);
        }
        this.semester = semester;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        if (contactDetails == null) {
            throw new IllegalArgumentException("Contact details cannot be null.");
        }
        this.contactDetails = contactDetails;
    }

    // Convenience delegators to encapsulated ContactDetails
    public String getEmail() {
        return contactDetails != null ? contactDetails.getEmail() : "";
    }

    public void setEmail(String email) {
        if (this.contactDetails == null) {
            this.contactDetails = new ContactDetails();
        }
        this.contactDetails.setEmail(email);
    }

    public String getPhoneNumber() {
        return contactDetails != null ? contactDetails.getPhoneNumber() : "";
    }

    public void setPhoneNumber(String phoneNumber) {
        if (this.contactDetails == null) {
            this.contactDetails = new ContactDetails();
        }
        this.contactDetails.setPhoneNumber(phoneNumber);
    }

    // --------------------------------------------------------------------------
    // Identity & Comparison
    // --------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", department='" + department + '\'' +
                ", semester=" + semester +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhoneNumber() + '\'' +
                ", maxCourses=" + getMaxCourses() +
                '}';
    }
}
