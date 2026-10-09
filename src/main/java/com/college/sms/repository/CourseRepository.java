package com.college.sms.repository;

import com.college.sms.model.Course;
import java.util.Optional;

/**
 * Interface contract for Course entity persistence and specialized lookups.
 */
public interface CourseRepository extends Repository<Course, String> {

    Optional<Course> findByCourseCode(String courseCode);

    boolean existsByCourseCode(String courseCode);
}
