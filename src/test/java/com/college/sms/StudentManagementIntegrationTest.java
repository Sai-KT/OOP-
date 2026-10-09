package com.college.sms;

import com.college.sms.dto.CourseRequest;
import com.college.sms.dto.DashboardResponse;
import com.college.sms.dto.RegistrationRequest;
import com.college.sms.dto.StudentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentManagementIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("1. GET /api/students returns 200 OK and student list")
    void testGetAllStudentsEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/students", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("STU101"));
    }

    @Test
    @DisplayName("2. GET /api/courses returns 200 OK and course list")
    void testGetAllCoursesEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/courses", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("CS302"));
    }

    @Test
    @DisplayName("3. GET /api/registrations returns 200 OK")
    void testGetAllRegistrationsEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/registrations", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("4. GET /api/dashboard/summary returns dynamic analytics")
    void testGetDashboardSummaryEndpoint() {
        ResponseEntity<DashboardResponse> response = 
                restTemplate.getForEntity("/api/dashboard/summary", DashboardResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalStudents() > 0);
        assertTrue(response.getBody().getTotalCourses() > 0);
    }

    @Test
    @DisplayName("5. POST /api/students creates student and returns 201 CREATED")
    void testCreateStudentEndpoint() {
        StudentRequest request = new StudentRequest(
                "INT_STU1", "Integration Student", "integration@college.edu", "9998887770", "CS", 2, "Undergraduate"
        );

        ResponseEntity<String> response = restTemplate.postForEntity("/api/students", request, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("INT_STU1"));
    }

    @Test
    @DisplayName("6. Duplicate POST /api/students returns 409 CONFLICT")
    void testDuplicateStudentReturnsConflict() {
        StudentRequest req = new StudentRequest(
                "DUP_INT", "Duplicate Int", "dup@college.edu", "9998887771", "IT", 1, "Undergraduate"
        );
        restTemplate.postForEntity("/api/students", req, String.class);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/students", req, String.class);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("already exists"));
    }

    @Test
    @DisplayName("7. Invalid StudentRequest returns 400 BAD REQUEST")
    void testInvalidStudentRequestReturnsBadRequest() {
        // Missing required fields
        StudentRequest badReq = new StudentRequest("", "", "invalid-email", "", "", -1, "");
        ResponseEntity<String> response = restTemplate.postForEntity("/api/students", badReq, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
