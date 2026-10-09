package com.college.sms.service;

import com.college.sms.exception.ActiveEnrollmentException;
import com.college.sms.exception.DuplicateStudentException;
import com.college.sms.exception.StudentNotFoundException;
import com.college.sms.model.Student;
import com.college.sms.repository.RegistrationRepository;
import com.college.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ==============================================================================
 * StudentService (OOP Concept: Business Logic, Abstraction & Method Overloading)
 * ==============================================================================
 * Manages all Student business rules, CRUD operations, and search features.
 * Demonstrates:
 * 1. Encapsulation of Domain Logic: Coordinates between repositories and enforces invariants.
 * 2. Method Overloading (Compile-Time Polymorphism):
 *    - searchStudents(String query) -> search by ID or Name
 *    - searchStudents(String department, String studentType) -> filter by department & type
 * 3. Referential Integrity: Rejects deletion of students who have active course enrollments.
 * ==============================================================================
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final RegistrationRepository registrationRepository;

    public StudentService(StudentRepository studentRepository, 
                          RegistrationRepository registrationRepository) {
        this.studentRepository = studentRepository;
        this.registrationRepository = registrationRepository;
    }

    /**
     * Creates and saves a new student after validating unique roll number.
     *
     * @param student Concrete Student instance (Undergraduate or Postgraduate)
     * @return Saved student
     */
    public Student createStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student payload cannot be null.");
        }

        String studentId = student.getStudentId();
        if (studentRepository.existsByStudentId(studentId)) {
            throw new DuplicateStudentException("Student with ID \"" + studentId + "\" already exists.");
        }

        return studentRepository.save(student);
    }

    /**
     * Retrieves all student records.
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Retrieves student by roll number, throwing StudentNotFoundException if absent.
     */
    public Student getStudentById(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }

        return studentRepository.findByStudentId(studentId.trim())
                .orElseThrow(() -> new StudentNotFoundException("Student with ID \"" + studentId + "\" was not found."));
    }

    /**
     * Updates an existing student's details.
     */
    public Student updateStudent(String studentId, Student updated) {
        Student existing = getStudentById(studentId);

        // If roll number is being changed, verify new roll number is not taken by another student
        String newStudentId = updated.getStudentId();
        if (!existing.getStudentId().equalsIgnoreCase(newStudentId) && 
            studentRepository.existsByStudentId(newStudentId)) {
            throw new DuplicateStudentException("Another student already has ID \"" + newStudentId + "\".");
        }

        // Transfer internal surrogate ID
        updated.setId(existing.getId());

        // If student roll changed, clean old key
        if (!existing.getStudentId().equalsIgnoreCase(newStudentId)) {
            studentRepository.deleteById(existing.getStudentId());
        }

        return studentRepository.save(updated);
    }

    /**
     * Deletes a student by ID, enforcing referential integrity.
     */
    public void deleteStudent(String studentId) {
        Student student = getStudentById(studentId);

        // Check if student has active course enrollments
        long activeEnrollments = registrationRepository.countByStudentId(student.getStudentId());
        if (activeEnrollments > 0) {
            throw new ActiveEnrollmentException(
                    "Cannot delete student " + student.getFullName() + " (" + student.getStudentId() + 
                    "): Student has " + activeEnrollments + " active course enrollment(s). Remove enrollments first.");
        }

        studentRepository.deleteById(student.getStudentId());
    }

    // --------------------------------------------------------------------------
    // Method Overloading (Compile-Time Polymorphism - Unit II Syllabus)
    // --------------------------------------------------------------------------

    /**
     * Overloaded Search 1: Searches by keyword matching studentId or fullName.
     *
     * @param query Search keyword
     * @return Matching students
     */
    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }

        String q = query.trim().toLowerCase();
        List<Student> results = new ArrayList<>();
        for (Student s : studentRepository.findAll()) {
            if (s.getStudentId().toLowerCase().contains(q) || 
                s.getFullName().toLowerCase().contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Overloaded Search 2: Searches by combined department and student type filters.
     *
     * @param department  Academic department name
     * @param studentType "Undergraduate" or "Postgraduate"
     * @return Matching students
     */
    public List<Student> searchStudents(String department, String studentType) {
        List<Student> results = new ArrayList<>();
        boolean checkDept = department != null && !department.trim().isEmpty();
        boolean checkType = studentType != null && !studentType.trim().isEmpty();

        for (Student s : studentRepository.findAll()) {
            boolean matchesDept = !checkDept || s.getDepartment().equalsIgnoreCase(department.trim());
            boolean matchesType = !checkType || s.getStudentType().equalsIgnoreCase(studentType.trim());
            if (matchesDept && matchesType) {
                results.add(s);
            }
        }
        return results;
    }
}
