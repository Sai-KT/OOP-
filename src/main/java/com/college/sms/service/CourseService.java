package com.college.sms.service;

import com.college.sms.exception.ActiveEnrollmentException;
import com.college.sms.exception.CourseNotFoundException;
import com.college.sms.exception.DuplicateCourseException;
import com.college.sms.model.Course;
import com.college.sms.repository.CourseRepository;
import com.college.sms.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ==============================================================================
 * CourseService (OOP Concept: Business Logic & Method Overloading)
 * ==============================================================================
 * Encapsulates all operations regarding courses and curricula.
 * Demonstrates:
 * 1. Method Overloading (Compile-Time Polymorphism):
 *    - searchCourses(String query) -> match by code or name
 *    - searchCourses(int minCredits, int maxCredits) -> match by credit range
 * 2. Referential Integrity: Rejects deletion of courses with active enrollments.
 * ==============================================================================
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final RegistrationRepository registrationRepository;

    public CourseService(CourseRepository courseRepository, 
                         RegistrationRepository registrationRepository) {
        this.courseRepository = courseRepository;
        this.registrationRepository = registrationRepository;
    }

    /**
     * Creates and saves a new course, validating unique code.
     */
    public Course createCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course payload cannot be null.");
        }

        String code = course.getCourseCode();
        if (courseRepository.existsByCourseCode(code)) {
            throw new DuplicateCourseException("Course with code \"" + code + "\" already exists.");
        }

        return courseRepository.save(course);
    }

    /**
     * Retrieves all course offerings.
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Finds course by code or throws CourseNotFoundException.
     */
    public Course getCourseByCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty.");
        }

        return courseRepository.findByCourseCode(courseCode.trim())
                .orElseThrow(() -> new CourseNotFoundException("Course with code \"" + courseCode + "\" was not found."));
    }

    /**
     * Updates an existing course.
     */
    public Course updateCourse(String courseCode, Course updated) {
        Course existing = getCourseByCode(courseCode);

        String newCode = updated.getCourseCode();
        if (!existing.getCourseCode().equalsIgnoreCase(newCode) && 
            courseRepository.existsByCourseCode(newCode)) {
            throw new DuplicateCourseException("Another course already uses code \"" + newCode + "\".");
        }

        updated.setId(existing.getId());

        if (!existing.getCourseCode().equalsIgnoreCase(newCode)) {
            courseRepository.deleteById(existing.getCourseCode());
        }

        return courseRepository.save(updated);
    }

    /**
     * Deletes a course if no active registrations exist.
     */
    public void deleteCourse(String courseCode) {
        Course course = getCourseByCode(courseCode);

        long activeRegistrations = registrationRepository.countByCourseCode(course.getCourseCode());
        if (activeRegistrations > 0) {
            throw new ActiveEnrollmentException(
                    "Cannot delete course " + course.getCourseCode() + " (" + course.getCourseName() + 
                    "): Course has " + activeRegistrations + " active student enrollment(s). Deregister students first.");
        }

        courseRepository.deleteById(course.getCourseCode());
    }

    // --------------------------------------------------------------------------
    // Method Overloading (Compile-Time Polymorphism - Unit II Syllabus)
    // --------------------------------------------------------------------------

    /**
     * Overloaded Search 1: Search by text keyword matching code or name.
     */
    public List<Course> searchCourses(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCourses();
        }

        String q = query.trim().toLowerCase();
        List<Course> results = new ArrayList<>();
        for (Course c : courseRepository.findAll()) {
            if (c.getCourseCode().toLowerCase().contains(q) || 
                c.getCourseName().toLowerCase().contains(q)) {
                results.add(c);
            }
        }
        return results;
    }

    /**
     * Overloaded Search 2: Filter by credit limits.
     */
    public List<Course> searchCourses(int minCredits, int maxCredits) {
        List<Course> results = new ArrayList<>();
        for (Course c : courseRepository.findAll()) {
            if (c.getCredits() >= minCredits && c.getCredits() <= maxCredits) {
                results.add(c);
            }
        }
        return results;
    }
}
