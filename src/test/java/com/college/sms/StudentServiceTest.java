package com.college.sms;

import com.college.sms.exception.ActiveEnrollmentException;
import com.college.sms.exception.DuplicateStudentException;
import com.college.sms.exception.StudentNotFoundException;
import com.college.sms.model.PostgraduateStudent;
import com.college.sms.model.Student;
import com.college.sms.model.UndergraduateStudent;
import com.college.sms.repository.RegistrationRepository;
import com.college.sms.repository.StudentRepository;
import com.college.sms.repository.file.FileRegistrationRepository;
import com.college.sms.repository.file.FileStudentRepository;
import com.college.sms.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    @TempDir
    Path tempDir;

    private StudentService studentService;
    private StudentRepository studentRepository;
    private RegistrationRepository registrationRepository;

    @BeforeEach
    void setUp() {
        Path studentFile = tempDir.resolve("students.csv");
        Path regFile = tempDir.resolve("registrations.csv");

        studentRepository = new FileStudentRepository(studentFile);
        registrationRepository = new FileRegistrationRepository(regFile);

        ((FileStudentRepository) studentRepository).init();
        ((FileRegistrationRepository) registrationRepository).init();

        studentService = new StudentService(studentRepository, registrationRepository);
    }

    @Test
    @DisplayName("1. Student creation works and assigns ID")
    void testCreateStudentSuccess() {
        Student student = new UndergraduateStudent(
                null, "TEST999", "Arya Stark", "arya@winterfell.edu", "9988776655", "Computer Science", 3
        );

        Student created = studentService.createStudent(student);
        assertNotNull(created.getId());
        assertEquals("TEST999", created.getStudentId());
        assertEquals("Arya Stark", created.getFullName());
    }

    @Test
    @DisplayName("2. Duplicate student ID is rejected with DuplicateStudentException")
    void testDuplicateStudentIdRejected() {
        Student s1 = new UndergraduateStudent(
                null, "DUP001", "Jon Snow", "jon@wall.edu", "9988771122", "IT", 1
        );
        studentService.createStudent(s1);

        Student s2 = new PostgraduateStudent(
                null, "DUP001", "Aegon Targaryen", "aegon@wall.edu", "9988771123", "IT", 1
        );

        assertThrows(DuplicateStudentException.class, () -> studentService.createStudent(s2));
    }

    @Test
    @DisplayName("3. Student updates work accurately")
    void testUpdateStudent() {
        Student student = studentService.getStudentById("STU101");
        student.setFullName("Rahul Sharma Updated");
        student.setSemester(5);

        Student updated = studentService.updateStudent("STU101", student);
        assertEquals("Rahul Sharma Updated", updated.getFullName());
        assertEquals(5, updated.getSemester());
    }

    @Test
    @DisplayName("4. Student deletion works when no active enrollments exist")
    void testDeleteStudentWithoutEnrollments() {
        Student student = new UndergraduateStudent(
                null, "DEL001", "Temp Student", "temp@college.edu", "9000000000", "Civil", 2
        );
        studentService.createStudent(student);

        assertTrue(studentRepository.existsByStudentId("DEL001"));
        studentService.deleteStudent("DEL001");
        assertFalse(studentRepository.existsByStudentId("DEL001"));
    }

    @Test
    @DisplayName("5. Student deletion is rejected if student has active course enrollments")
    void testDeleteStudentWithActiveEnrollmentsRejected() {
        // STU101 has seeded registrations in initial data
        assertThrows(ActiveEnrollmentException.class, () -> studentService.deleteStudent("STU101"));
    }

    @Test
    @DisplayName("6. Method overloading search filters students by query or department/type")
    void testOverloadedSearchMethods() {
        List<Student> searchById = studentService.searchStudents("STU101");
        assertFalse(searchById.isEmpty());
        assertEquals("STU101", searchById.get(0).getStudentId());

        List<Student> searchByDeptAndType = studentService.searchStudents("Computer Science", "Undergraduate");
        assertFalse(searchByDeptAndType.isEmpty());
        for (Student s : searchByDeptAndType) {
            assertEquals("Computer Science", s.getDepartment());
            assertEquals("Undergraduate", s.getStudentType());
        }
    }
}
