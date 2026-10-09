package com.college.sms;

import com.college.sms.exception.ActiveEnrollmentException;
import com.college.sms.exception.CourseNotFoundException;
import com.college.sms.exception.DuplicateCourseException;
import com.college.sms.model.Course;
import com.college.sms.repository.CourseRepository;
import com.college.sms.repository.RegistrationRepository;
import com.college.sms.repository.file.FileCourseRepository;
import com.college.sms.repository.file.FileRegistrationRepository;
import com.college.sms.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseServiceTest {

    @TempDir
    Path tempDir;

    private CourseService courseService;
    private CourseRepository courseRepository;
    private RegistrationRepository registrationRepository;

    @BeforeEach
    void setUp() {
        Path courseFile = tempDir.resolve("courses.csv");
        Path regFile = tempDir.resolve("registrations.csv");

        courseRepository = new FileCourseRepository(courseFile);
        registrationRepository = new FileRegistrationRepository(regFile);

        ((FileCourseRepository) courseRepository).init();
        ((FileRegistrationRepository) registrationRepository).init();

        courseService = new CourseService(courseRepository, registrationRepository);
    }

    @Test
    @DisplayName("1. Course creation works with full parameters and overloaded constructor")
    void testCreateCourseSuccess() {
        Course course1 = new Course(null, "CS405", "Distributed Systems", 4, 30);
        Course saved1 = courseService.createCourse(course1);
        assertEquals("CS405", saved1.getCourseCode());
        assertEquals(30, saved1.getMaxCapacity());

        // Constructor Overloading (default capacity of 40)
        Course course2 = new Course(null, "CS406", "Cloud Computing", 3);
        Course saved2 = courseService.createCourse(course2);
        assertEquals(Course.DEFAULT_CAPACITY, saved2.getMaxCapacity());
    }

    @Test
    @DisplayName("2. Duplicate course code is rejected")
    void testDuplicateCourseCodeRejected() {
        Course c1 = new Course(null, "DUP101", "Course One", 3, 30);
        courseService.createCourse(c1);

        Course c2 = new Course(null, "DUP101", "Course Two", 4, 40);
        assertThrows(DuplicateCourseException.class, () -> courseService.createCourse(c2));
    }

    @Test
    @DisplayName("3. Invalid course credits and capacity are rejected by encapsulated setters")
    void testInvalidCourseParametersRejected() {
        Course c = new Course();
        assertThrows(IllegalArgumentException.class, () -> c.setCredits(0));
        assertThrows(IllegalArgumentException.class, () -> c.setCredits(-2));
        assertThrows(IllegalArgumentException.class, () -> c.setMaxCapacity(0));
        assertThrows(IllegalArgumentException.class, () -> c.setMaxCapacity(-10));
    }

    @Test
    @DisplayName("4. Course deletion is rejected if active registrations exist")
    void testDeleteCourseWithActiveRegistrationsRejected() {
        // CS302 has active seed enrollments
        assertThrows(ActiveEnrollmentException.class, () -> courseService.deleteCourse("CS302"));
    }

    @Test
    @DisplayName("5. Course deletion succeeds when no enrollments exist")
    void testDeleteCourseWithoutEnrollments() {
        Course course = new Course(null, "FREE101", "Elective Workshop", 2, 20);
        courseService.createCourse(course);

        assertTrue(courseRepository.existsByCourseCode("FREE101"));
        courseService.deleteCourse("FREE101");
        assertFalse(courseRepository.existsByCourseCode("FREE101"));
    }

    @Test
    @DisplayName("6. Overloaded search methods return courses matching query or credit boundaries")
    void testOverloadedCourseSearch() {
        List<Course> searchByKeyword = courseService.searchCourses("Java");
        assertFalse(searchByKeyword.isEmpty());
        assertTrue(searchByKeyword.get(0).getCourseName().contains("Java"));

        List<Course> searchByCredits = courseService.searchCourses(4, 4);
        assertFalse(searchByCredits.isEmpty());
        for (Course c : searchByCredits) {
            assertEquals(4, c.getCredits());
        }
    }
}
