package com.college.sms;

import com.college.sms.adapter.LegacyStudentAdapter;
import com.college.sms.model.Course;
import com.college.sms.model.Student;
import com.college.sms.model.UndergraduateStudent;
import com.college.sms.repository.file.FileCourseRepository;
import com.college.sms.repository.file.FileStudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FilePersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("1. Student and Course records survive simulated backend restart")
    void testDataSurvivesApplicationRestart() {
        Path studentFile = tempDir.resolve("students_restart.csv");
        Path courseFile = tempDir.resolve("courses_restart.csv");

        // 1. Initial Application Instance
        FileStudentRepository studentRepo1 = new FileStudentRepository(studentFile);
        FileCourseRepository courseRepo1 = new FileCourseRepository(courseFile);
        studentRepo1.init();
        courseRepo1.init();

        // Add unique test records
        Student s = new UndergraduateStudent(null, "PERSIST01", "Persistence Test", "persist@test.edu", "9191919191", "CS", 4);
        studentRepo1.save(s);

        Course c = new Course(null, "PERSIST_CS", "Persistence Course", 4, 30);
        courseRepo1.save(c);

        // 2. Simulate Backend Shutdown and Restart (new repository instances reading same files)
        FileStudentRepository studentRepo2 = new FileStudentRepository(studentFile);
        FileCourseRepository courseRepo2 = new FileCourseRepository(courseFile);
        studentRepo2.init();
        courseRepo2.init();

        // 3. Verify records still exist intact after restart
        assertTrue(studentRepo2.existsByStudentId("PERSIST01"));
        assertEquals("Persistence Test", studentRepo2.findByStudentId("PERSIST01").get().getFullName());

        assertTrue(courseRepo2.existsByCourseCode("PERSIST_CS"));
        assertEquals("Persistence Course", courseRepo2.findByCourseCode("PERSIST_CS").get().getCourseName());
    }

    @Test
    @DisplayName("2. Special characters and embedded commas in course titles are safely handled")
    void testEmbeddedCommasAndQuotesHandling() {
        Path courseFile = tempDir.resolve("special_courses.csv");

        FileCourseRepository courseRepo = new FileCourseRepository(courseFile);
        courseRepo.init();

        // Name with comma and quote
        Course specialCourse = new Course(null, "SPEC909", "Data Science, AI & \"Deep Learning\"", 4, 25);
        courseRepo.save(specialCourse);

        // Restart repo
        FileCourseRepository reloadedRepo = new FileCourseRepository(courseFile);
        reloadedRepo.init();

        Course loaded = reloadedRepo.findByCourseCode("SPEC909").orElse(null);
        assertNotNull(loaded);
        assertEquals("Data Science, AI & \"Deep Learning\"", loaded.getCourseName());
    }

    @Test
    @DisplayName("3. Corrupted or invalid CSV rows are safely skipped without throwing unhandled exceptions")
    void testCorruptedCsvLinesHandledGracefully() throws Exception {
        Path studentFile = tempDir.resolve("corrupted_students.csv");
        String badContent = "id,studentId,fullName,email,phoneNumber,department,semester,studentType\n" +
                            "1,VALID01,Valid Student,valid@test.edu,999,CS,1,Undergraduate\n" +
                            "CORRUPTED_ROW_WITHOUT_ENOUGH_COLUMNS\n" +
                            "2,VALID02,Second Valid,second@test.edu,888,IT,2,Postgraduate\n";

        Files.writeString(studentFile, badContent);

        FileStudentRepository repo = new FileStudentRepository(studentFile);
        assertDoesNotThrow(repo::init);

        assertTrue(repo.existsByStudentId("VALID01"));
        assertTrue(repo.existsByStudentId("VALID02"));
    }

    @Test
    @DisplayName("4. LegacyStudentAdapter (Adapter Pattern) adapts legacy CSV maps into Student domain entities")
    void testLegacyStudentAdapter() {
        Map<String, String> legacyMap = new HashMap<>();
        legacyMap.put("roll_number", "LEGACY101");
        legacyMap.put("student_name", "Vikram Sarabhai");
        legacyMap.put("email_id", "vikram@isro.gov.in");
        legacyMap.put("phone", "9876500000");
        legacyMap.put("branch_name", "Aerospace Engineering");
        legacyMap.put("current_term", "8");
        legacyMap.put("program_type", "M.Tech"); // Postgraduate

        Student adapted = LegacyStudentAdapter.adaptFromLegacyRecord(legacyMap);

        assertEquals("LEGACY101", adapted.getStudentId());
        assertEquals("Vikram Sarabhai", adapted.getFullName());
        assertEquals("Aerospace Engineering", adapted.getDepartment());
        assertEquals(8, adapted.getSemester());
        assertEquals("Postgraduate", adapted.getStudentType());
        assertEquals(4, adapted.getMaxCourses()); // Postgraduate course limit!
    }
}
