/**
 * ============================================================================
 * Student Management System - API Service Module
 * ============================================================================
 * 
 * This module centralizes all communication between the frontend and the 
 * Java Spring Boot backend using the browser's native fetch() API.
 * 
 * Expected Spring Boot REST Endpoints:
 * ----------------------------------------------------------------------------
 * 1. Students:
 *    - GET    /api/students       -> Returns list of all students
 *    - GET    /api/students/{id}  -> Returns details of single student
 *    - POST   /api/students       -> Creates a new student record
 *    - PUT    /api/students/{id}  -> Updates an existing student
 *    - DELETE /api/students/{id}  -> Deletes student by ID
 * 
 * 2. Courses:
 *    - GET    /api/courses        -> Returns list of all courses
 *    - POST   /api/courses        -> Creates a new course
 *    - PUT    /api/courses/{id}   -> Updates a course
 *    - DELETE /api/courses/{id}   -> Deletes a course
 * 
 * 3. Registrations / Enrollments:
 *    - GET    /api/registrations      -> Returns list of all registrations
 *    - POST   /api/registrations      -> Registers student to a course
 *    - DELETE /api/registrations/{id}  -> Removes a registration
 * 
 * ============================================================================
 */

// Base URL for Spring Boot backend API
const API_BASE_URL = ''; // Relative path so it works seamlessly on http://localhost:8080/

// Configuration for API service
const ApiConfig = {
    // Default to Live Spring Boot Java backend
    useMock: false,
    
    // Switch between Mock mode and Live Spring Boot backend
    setMockMode(enable) {
        this.useMock = Boolean(enable);
        console.log(`[API Config] Mode set to: ${this.useMock ? 'MOCK DEMO' : 'LIVE SPRING BOOT'}`);
    },

    isMockMode() {
        return this.useMock;
    }
};

/* ----------------------------------------------------------------------------
 * Initial Mock In-Memory Data (Used ONLY when Mock Mode is explicitly enabled)
 * ---------------------------------------------------------------------------- */
let mockStudents = [
    {
        id: 1,
        studentId: "STU101",
        fullName: "Rahul Sharma",
        email: "rahul.sharma@college.edu",
        phoneNumber: "9876543210",
        department: "Computer Science",
        semester: 4,
        studentType: "Undergraduate"
    },
    {
        id: 2,
        studentId: "STU102",
        fullName: "Priya Patel",
        email: "priya.patel@college.edu",
        phoneNumber: "9823456781",
        department: "Information Technology",
        semester: 6,
        studentType: "Undergraduate"
    },
    {
        id: 3,
        studentId: "STU103",
        fullName: "Aman Verma",
        email: "aman.verma@college.edu",
        phoneNumber: "9123456789",
        department: "Computer Science",
        semester: 2,
        studentType: "Postgraduate"
    },
    {
        id: 4,
        studentId: "STU104",
        fullName: "Sneha Nair",
        email: "sneha.nair@college.edu",
        phoneNumber: "9786543210",
        department: "Electronics & Communication",
        semester: 4,
        studentType: "Undergraduate"
    }
];

let mockCourses = [
    {
        id: 1,
        courseCode: "CS201",
        courseName: "Data Structures & Algorithms",
        credits: 4,
        maxCapacity: 40
    },
    {
        id: 2,
        courseCode: "CS302",
        courseName: "Object Oriented Programming (Java)",
        credits: 4,
        maxCapacity: 50
    },
    {
        id: 3,
        courseCode: "IT301",
        courseName: "Database Management Systems",
        credits: 3,
        maxCapacity: 45
    },
    {
        id: 4,
        courseCode: "EC204",
        courseName: "Digital Logic Design",
        credits: 3,
        maxCapacity: 35
    }
];

let mockRegistrations = [
    {
        id: 1,
        studentId: "STU101",
        studentName: "Rahul Sharma",
        courseCode: "CS302",
        courseName: "Object Oriented Programming (Java)"
    },
    {
        id: 2,
        studentId: "STU102",
        studentName: "Priya Patel",
        courseCode: "CS201",
        courseName: "Data Structures & Algorithms"
    },
    {
        id: 3,
        studentId: "STU103",
        studentName: "Aman Verma",
        courseCode: "IT301",
        courseName: "Database Management Systems"
    }
];

// Helper to simulate short asynchronous delay in mock mode
const mockDelay = (ms = 120) => new Promise(resolve => setTimeout(resolve, ms));

/* ----------------------------------------------------------------------------
 * Helper: Native fetch wrapper with clear error handling
 * ---------------------------------------------------------------------------- */
async function request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    
    // Default headers
    const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        ...(options.headers || {})
    };

    try {
        const response = await fetch(url, {
            ...options,
            headers
        });

        // 204 No Content (often returned on successful DELETE)
        if (response.status === 204) {
            return null;
        }

        // Try parsing JSON response if present
        let responseData = null;
        const text = await response.text();
        if (text) {
            try {
                responseData = JSON.parse(text);
            } catch (e) {
                responseData = text;
            }
        }

        // Handle HTTP error statuses (4xx, 5xx)
        if (!response.ok) {
            const errorMessage = (responseData && responseData.message) 
                ? responseData.message 
                : (typeof responseData === 'string' ? responseData : `HTTP Error ${response.status}: ${response.statusText}`);
            
            const error = new Error(errorMessage);
            error.status = response.status;
            error.data = responseData;
            throw error;
        }

        return responseData;
    } catch (err) {
        // Clear message if backend server is unreachable
        if (err.name === 'TypeError' && err.message.includes('fetch')) {
            throw new Error(`Cannot connect to Spring Boot backend at ${url}. Please ensure your Spring Boot server is running on port 8080.`);
        }
        throw err;
    }
}

/* ----------------------------------------------------------------------------
 * Student API Services
 * ---------------------------------------------------------------------------- */
const StudentAPI = {
    // GET /api/students
    async getAll() {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            return [...mockStudents];
        }
        return await request('/api/students', { method: 'GET' });
    },

    // GET /api/students/{id}
    async getById(id) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            const student = mockStudents.find(s => String(s.id) === String(id) || s.studentId === String(id));
            if (!student) throw new Error(`Student with ID ${id} not found.`);
            return { ...student };
        }
        return await request(`/api/students/${id}`, { method: 'GET' });
    },

    // POST /api/students
    async create(studentData) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            // Validate unique studentId
            const exists = mockStudents.some(s => s.studentId.toLowerCase() === studentData.studentId.trim().toLowerCase());
            if (exists) {
                throw new Error(`Student with ID "${studentData.studentId}" already exists.`);
            }

            const newStudent = {
                id: Date.now(),
                ...studentData,
                semester: Number(studentData.semester)
            };
            mockStudents.unshift(newStudent);
            return { ...newStudent };
        }

        return await request('/api/students', {
            method: 'POST',
            body: JSON.stringify(studentData)
        });
    },

    // PUT /api/students/{id}
    async update(id, studentData) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            const index = mockStudents.findIndex(s => String(s.id) === String(id) || s.studentId === String(id));
            if (index === -1) throw new Error(`Student with ID ${id} not found.`);

            // Check if studentId changed and conflicts with another record
            const conflict = mockStudents.some((s, idx) => 
                idx !== index && s.studentId.toLowerCase() === studentData.studentId.trim().toLowerCase()
            );
            if (conflict) {
                throw new Error(`Another student already uses Student ID "${studentData.studentId}".`);
            }

            mockStudents[index] = {
                ...mockStudents[index],
                ...studentData,
                semester: Number(studentData.semester)
            };

            // Also update studentName in mock registrations if changed
            mockRegistrations.forEach(reg => {
                if (reg.studentId === mockStudents[index].studentId) {
                    reg.studentName = mockStudents[index].fullName;
                }
            });

            return { ...mockStudents[index] };
        }

        return await request(`/api/students/${id}`, {
            method: 'PUT',
            body: JSON.stringify(studentData)
        });
    },

    // DELETE /api/students/{id}
    async delete(id) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            const student = mockStudents.find(s => String(s.id) === String(id) || s.studentId === String(id));
            if (!student) throw new Error(`Student with ID ${id} not found.`);

            // Remove student
            mockStudents = mockStudents.filter(s => String(s.id) !== String(id) && s.studentId !== String(id));

            // Cascade delete registrations in mock mode
            mockRegistrations = mockRegistrations.filter(r => r.studentId !== student.studentId);
            return true;
        }

        return await request(`/api/students/${id}`, { method: 'DELETE' });
    }
};

/* ----------------------------------------------------------------------------
 * Course API Services
 * ---------------------------------------------------------------------------- */
const CourseAPI = {
    // GET /api/courses
    async getAll() {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            return [...mockCourses];
        }
        return await request('/api/courses', { method: 'GET' });
    },

    // GET /api/courses/{id}
    async getById(id) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            const course = mockCourses.find(c => String(c.id) === String(id) || c.courseCode === String(id));
            if (!course) throw new Error(`Course ${id} not found.`);
            return { ...course };
        }
        return await request(`/api/courses/${id}`, { method: 'GET' });
    },

    // POST /api/courses
    async create(courseData) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            // Validate unique course code
            const exists = mockCourses.some(c => c.courseCode.toLowerCase() === courseData.courseCode.trim().toLowerCase());
            if (exists) {
                throw new Error(`Course with code "${courseData.courseCode}" already exists.`);
            }

            const newCourse = {
                id: Date.now(),
                ...courseData,
                credits: Number(courseData.credits),
                maxCapacity: Number(courseData.maxCapacity)
            };
            mockCourses.unshift(newCourse);
            return { ...newCourse };
        }

        return await request('/api/courses', {
            method: 'POST',
            body: JSON.stringify(courseData)
        });
    },

    // PUT /api/courses/{id}
    async update(id, courseData) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            const index = mockCourses.findIndex(c => String(c.id) === String(id) || c.courseCode === String(id));
            if (index === -1) throw new Error(`Course ${id} not found.`);

            const conflict = mockCourses.some((c, idx) => 
                idx !== index && c.courseCode.toLowerCase() === courseData.courseCode.trim().toLowerCase()
            );
            if (conflict) {
                throw new Error(`Another course already uses code "${courseData.courseCode}".`);
            }

            mockCourses[index] = {
                ...mockCourses[index],
                ...courseData,
                credits: Number(courseData.credits),
                maxCapacity: Number(courseData.maxCapacity)
            };

            // Also update course name in mock registrations
            mockRegistrations.forEach(reg => {
                if (reg.courseCode === mockCourses[index].courseCode) {
                    reg.courseName = mockCourses[index].courseName;
                }
            });

            return { ...mockCourses[index] };
        }

        return await request(`/api/courses/${id}`, {
            method: 'PUT',
            body: JSON.stringify(courseData)
        });
    },

    // DELETE /api/courses/{id}
    async delete(id) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            const course = mockCourses.find(c => String(c.id) === String(id) || c.courseCode === String(id));
            if (!course) throw new Error(`Course ${id} not found.`);

            mockCourses = mockCourses.filter(c => String(c.id) !== String(id) && c.courseCode !== String(id));
            // Cascade delete registrations in mock mode
            mockRegistrations = mockRegistrations.filter(r => r.courseCode !== course.courseCode);
            return true;
        }

        return await request(`/api/courses/${id}`, { method: 'DELETE' });
    }
};

/* ----------------------------------------------------------------------------
 * Registration / Enrollment API Services
 * ---------------------------------------------------------------------------- */
const RegistrationAPI = {
    // GET /api/registrations
    async getAll() {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            return [...mockRegistrations];
        }
        return await request('/api/registrations', { method: 'GET' });
    },

    // POST /api/registrations
    async create(registrationData) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            const { studentId, courseCode } = registrationData;

            // 1. Verify Student exists
            const student = mockStudents.find(s => s.studentId === studentId);
            if (!student) throw new Error(`Student with ID "${studentId}" does not exist.`);

            // 2. Verify Course exists
            const course = mockCourses.find(c => c.courseCode === courseCode);
            if (!course) throw new Error(`Course with Code "${courseCode}" does not exist.`);

            // 3. Duplicate Enrollment validation (Spring Boot OOP business rule)
            const isDuplicate = mockRegistrations.some(
                r => r.studentId === studentId && r.courseCode === courseCode
            );
            if (isDuplicate) {
                throw new Error(`Student ${student.fullName} (${studentId}) is already enrolled in ${course.courseName} (${courseCode}).`);
            }

            // 4. Capacity limit validation
            const currentEnrolledCount = mockRegistrations.filter(r => r.courseCode === courseCode).length;
            if (currentEnrolledCount >= course.maxCapacity) {
                throw new Error(`Course ${courseCode} has reached its maximum capacity of ${course.maxCapacity} students.`);
            }

            const newRegistration = {
                id: Date.now(),
                studentId: student.studentId,
                studentName: student.fullName,
                courseCode: course.courseCode,
                courseName: course.courseName
            };

            mockRegistrations.unshift(newRegistration);
            return { ...newRegistration };
        }

        // Live backend: sends payload to Spring Boot
        return await request('/api/registrations', {
            method: 'POST',
            body: JSON.stringify(registrationData)
        });
    },

    // DELETE /api/registrations/{id}
    async delete(id) {
        if (ApiConfig.isMockMode()) {
            await mockDelay();
            const index = mockRegistrations.findIndex(r => String(r.id) === String(id));
            if (index === -1) throw new Error(`Enrollment record ${id} not found.`);

            mockRegistrations.splice(index, 1);
            return true;
        }

        return await request(`/api/registrations/${id}`, { method: 'DELETE' });
    }
};

/* ----------------------------------------------------------------------------
 * Backend Health Checker
 * ---------------------------------------------------------------------------- */
async function checkBackendHealth() {
    try {
        const response = await fetch('/api/students', {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });
        return response.ok;
    } catch (e) {
        return false;
    }
}

// Expose APIs globally for app.js
window.ApiConfig = ApiConfig;
window.StudentAPI = StudentAPI;
window.CourseAPI = CourseAPI;
window.RegistrationAPI = RegistrationAPI;
window.checkBackendHealth = checkBackendHealth;
