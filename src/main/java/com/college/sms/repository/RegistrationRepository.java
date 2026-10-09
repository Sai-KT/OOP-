package com.college.sms.repository;

import com.college.sms.model.Registration;
import java.util.List;

/**
 * Interface contract for Registration persistence and query operations.
 */
public interface RegistrationRepository extends Repository<Registration, Long> {

    List<Registration> findByStudentId(String studentId);

    List<Registration> findByCourseCode(String courseCode);

    boolean existsByStudentIdAndCourseCode(String studentId, String courseCode);

    long countByCourseCode(String courseCode);

    long countByStudentId(String studentId);

    void deleteByStudentId(String studentId);

    void deleteByCourseCode(String courseCode);
}
