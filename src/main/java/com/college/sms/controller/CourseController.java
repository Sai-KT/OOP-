package com.college.sms.controller;

import com.college.sms.dto.CourseRequest;
import com.college.sms.model.Course;
import com.college.sms.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Course entity operations.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * GET /api/courses
     */
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(courseService.searchCourses(search));
        }
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * GET /api/courses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getCourseByCode(id));
    }

    /**
     * POST /api/courses
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseRequest request) {
        Course course = new Course(
                null,
                request.getCourseCode(),
                request.getCourseName(),
                request.getCredits(),
                request.getMaxCapacity()
        );

        Course saved = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * PUT /api/courses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable String id,
                                               @Valid @RequestBody CourseRequest request) {
        Course course = new Course(
                null,
                request.getCourseCode(),
                request.getCourseName(),
                request.getCredits(),
                request.getMaxCapacity()
        );

        Course updated = courseService.updateCourse(id, course);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/courses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
