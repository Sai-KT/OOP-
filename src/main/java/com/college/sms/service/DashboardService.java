package com.college.sms.service;

import com.college.sms.dto.DashboardResponse;
import com.college.sms.model.Course;
import com.college.sms.model.Student;
import com.college.sms.repository.CourseRepository;
import com.college.sms.repository.RegistrationRepository;
import com.college.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ==============================================================================
 * DashboardService (OOP Concept: Aggregation & Dynamic Calculations)
 * ==============================================================================
 * Dynamically computes real-time academic administration statistics.
 * Demonstrates:
 * 1. Information Aggregation: Computes metrics across Students, Courses, and Registrations.
 * 2. Real-Time Calculation: Never hardcodes statistics; aggregates live collections.
 * ==============================================================================
 */
@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RegistrationRepository registrationRepository;

    public DashboardService(StudentRepository studentRepository,
                            CourseRepository courseRepository,
                            RegistrationRepository registrationRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.registrationRepository = registrationRepository;
    }

    /**
     * Aggregates live system data into a summary DTO.
     */
    public DashboardResponse getSummary() {
        List<Student> students = studentRepository.findAll();
        List<Course> courses = courseRepository.findAll();
        long totalEnrollments = registrationRepository.count();

        long ugCount = 0;
        long pgCount = 0;
        for (Student s : students) {
            if ("Postgraduate".equalsIgnoreCase(s.getStudentType())) {
                pgCount++;
            } else {
                ugCount++;
            }
        }

        long totalCapacity = 0;
        for (Course c : courses) {
            totalCapacity += c.getMaxCapacity();
        }

        double occupancyRate = totalCapacity > 0 
                ? Math.round(((double) totalEnrollments / totalCapacity) * 1000.0) / 10.0 
                : 0.0;

        return new DashboardResponse(
                students.size(),
                courses.size(),
                totalEnrollments,
                ugCount,
                pgCount,
                totalCapacity,
                occupancyRate
        );
    }
}
