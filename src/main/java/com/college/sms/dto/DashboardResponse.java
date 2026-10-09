package com.college.sms.dto;

/**
 * Data Transfer Object for Dashboard analytical metrics.
 */
public class DashboardResponse {

    private long totalStudents;
    private long totalCourses;
    private long totalEnrollments;
    private long undergraduateCount;
    private long postgraduateCount;
    private long totalCourseCapacity;
    private double occupancyRate;

    public DashboardResponse() {}

    public DashboardResponse(long totalStudents, long totalCourses, long totalEnrollments,
                             long undergraduateCount, long postgraduateCount,
                             long totalCourseCapacity, double occupancyRate) {
        this.totalStudents = totalStudents;
        this.totalCourses = totalCourses;
        this.totalEnrollments = totalEnrollments;
        this.undergraduateCount = undergraduateCount;
        this.postgraduateCount = postgraduateCount;
        this.totalCourseCapacity = totalCourseCapacity;
        this.occupancyRate = occupancyRate;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public long getTotalEnrollments() {
        return totalEnrollments;
    }

    public void setTotalEnrollments(long totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }

    public long getUndergraduateCount() {
        return undergraduateCount;
    }

    public void setUndergraduateCount(long undergraduateCount) {
        this.undergraduateCount = undergraduateCount;
    }

    public long getPostgraduateCount() {
        return postgraduateCount;
    }

    public void setPostgraduateCount(long postgraduateCount) {
        this.postgraduateCount = postgraduateCount;
    }

    public long getTotalCourseCapacity() {
        return totalCourseCapacity;
    }

    public void setTotalCourseCapacity(long totalCourseCapacity) {
        this.totalCourseCapacity = totalCourseCapacity;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }
}
