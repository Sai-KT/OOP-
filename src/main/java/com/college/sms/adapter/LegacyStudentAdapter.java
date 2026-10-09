package com.college.sms.adapter;

import com.college.sms.model.PostgraduateStudent;
import com.college.sms.model.Student;
import com.college.sms.model.UndergraduateStudent;

import java.util.Map;

/**
 * ==============================================================================
 * LegacyStudentAdapter (OOP Concept: Adapter Design Pattern - Unit V Syllabus)
 * ==============================================================================
 * Adapts legacy university record formats into modern Student domain objects.
 * Demonstrates:
 * 1. Structural Design Pattern: Acts as a bridge between an incompatible legacy record
 *    and the target Student domain interface.
 * 2. Polymorphic Factory: Constructs either UndergraduateStudent or PostgraduateStudent
 *    depending on the adapted degree program.
 * ==============================================================================
 */
public class LegacyStudentAdapter {

    /**
     * Adapts legacy CSV/Map dictionary containing outdated column naming:
     * - "roll_number" -> studentId
     * - "student_name" -> fullName
     * - "email_id" -> email
     * - "phone" -> phoneNumber
     * - "branch_name" -> department
     * - "current_term" -> semester
     * - "program_type" -> studentType ("B.Tech"/"UG" or "M.Tech"/"PG")
     *
     * @param legacyRecord Map of legacy key-value pairs
     * @return Standard Student domain entity
     */
    public static Student adaptFromLegacyRecord(Map<String, String> legacyRecord) {
        if (legacyRecord == null) {
            throw new IllegalArgumentException("Legacy record cannot be null.");
        }

        String studentId = legacyRecord.getOrDefault("roll_number", "").trim();
        String fullName = legacyRecord.getOrDefault("student_name", "").trim();
        String email = legacyRecord.getOrDefault("email_id", "").trim();
        String phone = legacyRecord.getOrDefault("phone", "").trim();
        String department = legacyRecord.getOrDefault("branch_name", "General").trim();
        
        int semester = 1;
        try {
            semester = Integer.parseInt(legacyRecord.getOrDefault("current_term", "1").trim());
        } catch (NumberFormatException ignored) {
        }

        String programType = legacyRecord.getOrDefault("program_type", "UG").trim();
        boolean isPg = "PG".equalsIgnoreCase(programType) || 
                       "M.Tech".equalsIgnoreCase(programType) || 
                       "Postgraduate".equalsIgnoreCase(programType);

        if (isPg) {
            return new PostgraduateStudent(null, studentId, fullName, email, phone, department, semester);
        } else {
            return new UndergraduateStudent(null, studentId, fullName, email, phone, department, semester);
        }
    }
}
