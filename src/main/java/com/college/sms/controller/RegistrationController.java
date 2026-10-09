package com.college.sms.controller;

import com.college.sms.dto.RegistrationRequest;
import com.college.sms.model.Registration;
import com.college.sms.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Student Course Registrations.
 */
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * GET /api/registrations
     */
    @GetMapping
    public ResponseEntity<List<Registration>> getAllRegistrations(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String courseCode) {

        if (studentId != null && !studentId.trim().isEmpty()) {
            return ResponseEntity.ok(registrationService.getRegistrationsForStudent(studentId));
        }
        if (courseCode != null && !courseCode.trim().isEmpty()) {
            return ResponseEntity.ok(registrationService.getRegistrationsForCourse(courseCode));
        }

        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }

    /**
     * POST /api/registrations
     */
    @PostMapping
    public ResponseEntity<Registration> register(@Valid @RequestBody RegistrationRequest request) {
        Registration registration = registrationService.registerStudent(
                request.getStudentId(),
                request.getCourseCode()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(registration);
    }

    /**
     * DELETE /api/registrations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deregister(@PathVariable Long id) {
        registrationService.deregister(id);
        return ResponseEntity.noContent().build();
    }
}
