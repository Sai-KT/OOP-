package com.college.sms.service;

import com.college.sms.exception.CourseCapacityExceededException;
import com.college.sms.exception.InvalidRegistrationException;
import com.college.sms.exception.MaxEnrollmentLimitExceededException;
import com.college.sms.model.Course;
import com.college.sms.model.Registration;
import com.college.sms.model.Student;
import com.college.sms.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ==============================================================================
 * RegistrationService (OOP Concept: Runtime Polymorphism & Dynamic Method Dispatch)
 * ==============================================================================
 * Orchestrates the enrollment of students into university courses.
 * Demonstrates:
 * 1. Runtime Polymorphism: Works with the abstract Student base class. When calling
 *    student.getMaxCourses(), dynamic binding ensures that UndergraduateStudent returns 6
 *    and PostgraduateStudent returns 4 at runtime without instanceof checks.
 * 2. Association Management: Creates and manages Registration associations between
 *    Student and Course entities.
 * 3. Domain Invariants:
 *    - Student must exist
 *    - Course must exist
 *    - No duplicate enrollment in the same course
 *    - Course capacity cannot be exceeded
 *    - Student's polymorphic course maximum limit cannot be exceeded
 * ==============================================================================
 */
@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final StudentService studentService;
    private final CourseService courseService;

    public RegistrationService(RegistrationRepository registrationRepository,
                               StudentService studentService,
                               CourseService courseService) {
        this.registrationRepository = registrationRepository;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    /**
     * Enrolls a student in a course.
     * Crucial Viva Demonstration: Demonstrates Runtime Polymorphism!
     *
     * @param studentId  Roll number of student
     * @param courseCode Code of course
     * @return Created Registration entity
     */
    public Registration registerStudent(String studentId, String courseCode) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty.");
        }

        // 1. Verify Student exists (polymorphic instance: Undergraduate or Postgraduate)
        Student student = studentService.getStudentById(studentId.trim());

        // 2. Verify Course exists
        Course course = courseService.getCourseByCode(courseCode.trim());

        String sId = student.getStudentId();
        String cCode = course.getCourseCode();

        // 3. Prevent duplicate enrollment
        if (registrationRepository.existsByStudentIdAndCourseCode(sId, cCode)) {
            throw new InvalidRegistrationException(
                    "Student " + student.getFullName() + " (" + sId + ") is already registered for " + 
                    course.getCourseName() + " (" + cCode + ").");
        }

        // 4. Verify Course Capacity
        long currentEnrolledInCourse = registrationRepository.countByCourseCode(cCode);
        if (currentEnrolledInCourse >= course.getMaxCapacity()) {
            throw new CourseCapacityExceededException(
                    "Course " + cCode + " (" + course.getCourseName() + ") has reached its maximum capacity of " + 
                    course.getMaxCapacity() + " students.");
        }

        // 5. RUNTIME POLYMORPHISM & DYNAMIC METHOD BINDING:
        // Notice we do NOT check (if student instanceof UndergraduateStudent).
        // Instead, we invoke student.getMaxCourses() directly on the abstract reference.
        // Java resolves the method call at runtime to the specific subclass implementation!
        int allowedCourses = student.getMaxCourses();
        long currentStudentEnrollments = registrationRepository.countByStudentId(sId);

        if (currentStudentEnrollments >= allowedCourses) {
            throw new MaxEnrollmentLimitExceededException(
                    "Student " + student.getFullName() + " (" + sId + ") has reached the maximum enrollment limit of " + 
                    allowedCourses + " courses allowed for " + student.getStudentType() + " students.");
        }

        // 6. Create and persist Association
        Registration registration = new Registration(
                null,
                sId,
                student.getFullName(),
                cCode,
                course.getCourseName()
        );

        return registrationRepository.save(registration);
    }

    /**
     * Retrieves all registrations in the system.
     */
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    /**
     * Deregisters / removes a registration by its ID.
     */
    public void deregister(Long registrationId) {
        if (registrationId == null) {
            throw new IllegalArgumentException("Registration ID cannot be null.");
        }
        if (!registrationRepository.existsById(registrationId)) {
            throw new InvalidRegistrationException("Enrollment record #" + registrationId + " was not found.");
        }
        registrationRepository.deleteById(registrationId);
    }

    /**
     * Retrieves all courses a student is registered for.
     */
    public List<Registration> getRegistrationsForStudent(String studentId) {
        return registrationRepository.findByStudentId(studentId);
    }

    /**
     * Retrieves all students registered for a course.
     */
    public List<Registration> getRegistrationsForCourse(String courseCode) {
        return registrationRepository.findByCourseCode(courseCode);
    }
}
