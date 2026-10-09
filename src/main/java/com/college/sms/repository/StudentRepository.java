package com.college.sms.repository;

import com.college.sms.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Interface contract for Student entity persistence and specialized lookups.
 */
public interface StudentRepository extends Repository<Student, String> {

    Optional<Student> findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    List<Student> findByDepartment(String department);

    List<Student> findByStudentType(String studentType);
}
