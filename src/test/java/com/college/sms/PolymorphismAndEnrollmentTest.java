package com.college.sms;

import com.college.sms.exception.CourseCapacityExceededException;
import com.college.sms.exception.CourseNotFoundException;
import com.college.sms.exception.InvalidRegistrationException;
import com.college.sms.exception.MaxEnrollmentLimitExceededException;
import com.college.sms.exception.StudentNotFoundException;
import com.college.sms.model.Course;
import com.college.sms.model.PostgraduateStudent;
import com.college.sms.model.Registration;
import com.college.sms.model.Student;
import com.college.sms.model.UndergraduateStudent;
import com.college.sms.repository.CourseRepository;
import com.college.sms.repository.RegistrationRepository;
import com.college.sms.repository.StudentRepository;
import com.college.sms.repository.file.FileCourseRepository;
import com.college.sms.repository.file.FileRegistrationRepository;
import com.college.sms.repository.file.FileStudentRepository;
import com.college.sms.service.CourseService;
import com.college.sms.service.RegistrationService;
import com.college.sms.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PolymorphismAndEnrollmentTest {

    @TempDir
    Path tempDir;

    private RegistrationService registrationService;
    private StudentService studentService;
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        Path sFile = tempDir.resolve("students.csv");
        Path cFile = tempDir.resolve("courses.csv");
        Path rFile = tempDir.resolve("registrations.csv");

        StudentRepository studentRepository = new FileStudentRepository(sFile);
        CourseRepository courseRepository = new FileCourseRepository(cFile);
        RegistrationRepository registrationRepository = new FileRegistrationRepository(rFile);

        ((FileStudentRepository) studentRepository).init();
        ((FileCourseRepository) courseRepository).init();
        ((FileRegistrationRepository) registrationRepository).init();

        studentService = new StudentService(studentRepository, registrationRepository);
        courseService = new CourseService(courseRepository, registrationRepository);
        registrationService = new RegistrationService(registrationRepository, studentService, courseService);
    }

    @Test
    @DisplayName("1. Inheritance & Runtime Polymorphism: Different limits returned by Student subclasses")
    void testPolymorphicCourseLimits() {
        Student ug = new UndergraduateStudent(1L, "UG01", "UG Student", "ug@test.edu", "111", "CS", 1);
        Student pg = new PostgraduateStudent(2L, "PG01", "PG Student", "pg@test.edu", "222", "CS", 1);

        // Abstract references calling overridden method
        assertEquals(6, ug.getMaxCourses());
        assertEquals("Undergraduate", ug.getStudentType());

        assertEquals(4, pg.getMaxCourses());
        assertEquals("Postgraduate", pg.getStudentType());
    }

    @Test
    @DisplayName("2. Successful registration creates valid Association")
    void testSuccessfulRegistration() {
        // Enroll STU104 in CS201 (not currently enrolled in seed data)
        Registration reg = registrationService.registerStudent("STU104", "CS201");
        assertNotNull(reg.getId());
        assertEquals("STU104", reg.getStudentId());
        assertEquals("CS201", reg.getCourseCode());
    }

    @Test
    @DisplayName("3. Duplicate enrollment in same course is rejected")
    void testDuplicateEnrollmentRejected() {
        // STU101 is already enrolled in CS302 in seed data
        assertThrows(InvalidRegistrationException.class, () -> 
                registrationService.registerStudent("STU101", "CS302"));
    }

    @Test
    @DisplayName("4. Registration rejected when Course capacity is reached")
    void testCourseCapacityExceededRejected() {
        // Create course with capacity = 1
        Course smallCourse = new Course(null, "TINY101", "Tiny Seminar", 1, 1);
        courseService.createCourse(smallCourse);

        // Register 1st student -> succeeds
        registrationService.registerStudent("STU101", "TINY101");

        // Register 2nd student -> capacity exceeded exception
        assertThrows(CourseCapacityExceededException.class, () -> 
                registrationService.registerStudent("STU102", "TINY101"));
    }

    @Test
    @DisplayName("5. Registration rejected when Student exceeds polymorphic course limit")
    void testMaxEnrollmentLimitExceededRejected() {
        // Create a postgraduate student (max 4 courses)
        Student pgStudent = new PostgraduateStudent(
                null, "PG_LIMIT", "Research Scholar", "pg@test.edu", "1234567890", "CS", 1
        );
        studentService.createStudent(pgStudent);

        // Create 5 courses
        for (int i = 1; i <= 5; i++) {
            courseService.createCourse(new Course(null, "CAP" + i, "Cap Course " + i, 3, 50));
        }

        // Register in 4 courses (allowed maximum)
        registrationService.registerStudent("PG_LIMIT", "CAP1");
        registrationService.registerStudent("PG_LIMIT", "CAP2");
        registrationService.registerStudent("PG_LIMIT", "CAP3");
        registrationService.registerStudent("PG_LIMIT", "CAP4");

        // 5th course exceeds limit of 4 courses for Postgraduate student!
        assertThrows(MaxEnrollmentLimitExceededException.class, () -> 
                registrationService.registerStudent("PG_LIMIT", "CAP5"));
    }

    @Test
    @DisplayName("6. Missing student or missing course generates appropriate NotFound exceptions")
    void testMissingEntitiesRejected() {
        assertThrows(StudentNotFoundException.class, () -> 
                registrationService.registerStudent("NON_EXISTENT", "CS201"));

        assertThrows(CourseNotFoundException.class, () -> 
                registrationService.registerStudent("STU101", "NON_EXISTENT"));
    }
}
