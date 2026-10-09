package com.college.sms.controller;

import com.college.sms.dto.StudentRequest;
import com.college.sms.dto.StudentResponse;
import com.college.sms.model.PostgraduateStudent;
import com.college.sms.model.Student;
import com.college.sms.model.UndergraduateStudent;
import com.college.sms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Student entity operations.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * GET /api/students
     * Optional search and filtering query parameters supported.
     */
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type) {

        List<Student> students;
        if (department != null || type != null) {
            students = studentService.searchStudents(department, type);
        } else if (search != null) {
            students = studentService.searchStudents(search);
        } else {
            students = studentService.getAllStudents();
        }

        List<StudentResponse> response = students.stream()
                .map(StudentResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/students/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable String id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(StudentResponse.fromEntity(student));
    }

    /**
     * POST /api/students
     */
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        Student entity;
        if ("Postgraduate".equalsIgnoreCase(request.getStudentType())) {
            entity = new PostgraduateStudent(
                    null,
                    request.getStudentId(),
                    request.getFullName(),
                    request.getEmail(),
                    request.getPhoneNumber(),
                    request.getDepartment(),
                    request.getSemester()
            );
        } else {
            entity = new UndergraduateStudent(
                    null,
                    request.getStudentId(),
                    request.getFullName(),
                    request.getEmail(),
                    request.getPhoneNumber(),
                    request.getDepartment(),
                    request.getSemester()
            );
        }

        Student saved = studentService.createStudent(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(StudentResponse.fromEntity(saved));
    }

    /**
     * PUT /api/students/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable String id,
                                                         @Valid @RequestBody StudentRequest request) {
        Student entity;
        if ("Postgraduate".equalsIgnoreCase(request.getStudentType())) {
            entity = new PostgraduateStudent(
                    null,
                    request.getStudentId(),
                    request.getFullName(),
                    request.getEmail(),
                    request.getPhoneNumber(),
                    request.getDepartment(),
                    request.getSemester()
            );
        } else {
            entity = new UndergraduateStudent(
                    null,
                    request.getStudentId(),
                    request.getFullName(),
                    request.getEmail(),
                    request.getPhoneNumber(),
                    request.getDepartment(),
                    request.getSemester()
            );
        }

        Student updated = studentService.updateStudent(id, entity);
        return ResponseEntity.ok(StudentResponse.fromEntity(updated));
    }

    /**
     * DELETE /api/students/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
