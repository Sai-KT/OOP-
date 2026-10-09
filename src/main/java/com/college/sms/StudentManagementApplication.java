package com.college.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ==============================================================================
 * StudentManagementApplication
 * ==============================================================================
 * Entry point for the College Student Management System Spring Boot Application.
 * Boots the embedded Tomcat container, initializes file repositories, registers
 * controllers, and serves the static frontend at http://localhost:8080/
 * ==============================================================================
 */
@SpringBootApplication
public class StudentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("🎓 Student Management System (Java OOP Backend) is now LIVE!");
        System.out.println("🌐 Open in your browser: http://localhost:8080");
        System.out.println("📂 Local CSV Data Store: data/ (students.csv, courses.csv, registrations.csv)");
        System.out.println("==================================================================");
    }
}
