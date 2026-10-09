# 🎓 Student Management System (College OOP Mini-Project)

A complete, robust, and academic-oriented **Student Management System** developed in **Java 21 / Spring Boot** paired with an executive **HTML5, CSS3, and Vanilla JavaScript** dashboard.

Designed specifically to demonstrate all core **Object-Oriented Programming (OOP) concepts** covered across university syllabus **Units I through V**.

---

## 📁 Project Architecture & Package Structure

```text
OOP ag/
├── pom.xml                                   # Maven dependency management
├── README.md                                 # Complete project documentation
├── OOP_SYLLABUS_MAPPING.md                   # Unit-by-unit syllabus mapping table
├── PROJECT_EXPLANATION.md                    # Viva preparation guide & questions
├── data/                                     # Local CSV storage directory (Auto-created)
│   ├── students.csv                          # Stored student records
│   ├── courses.csv                           # Stored course records
│   └── registrations.csv                     # Stored enrollment associations
└── src/
    ├── main/
    │   ├── java/com/college/sms/
    │   │   ├── StudentManagementApplication.java  # Spring Boot Application entrypoint
    │   │   ├── model/
    │   │   │   ├── Student.java              # Abstract class (Abstraction & Inheritance root)
    │   │   │   ├── UndergraduateStudent.java # Subclass (Max 6 courses)
    │   │   │   ├── PostgraduateStudent.java  # Subclass (Max 4 courses)
    │   │   │   ├── ContactDetails.java       # Composition owned by Student
    │   │   │   ├── Course.java               # Encapsulation & Constructor Overloading
    │   │   │   └── Registration.java         # Association between Student & Course
    │   │   ├── repository/
    │   │   │   ├── Repository.java           # Generic Interface <T, ID>
    │   │   │   ├── StudentRepository.java    # Specialized Student interface
    │   │   │   ├── CourseRepository.java     # Specialized Course interface
    │   │   │   ├── RegistrationRepository.java # Specialized Registration interface
    │   │   │   └── file/
    │   │   │       ├── CsvHelper.java        # Character streams & atomic file moves
    │   │   │       ├── FileStudentRepository.java
    │   │   │       ├── FileCourseRepository.java
    │   │   │       └── FileRegistrationRepository.java (Demonstrates Iterator Pattern)
    │   │   ├── service/
    │   │   │   ├── StudentService.java       # Business rules & Method Overloading
    │   │   │   ├── CourseService.java        # Business rules & Method Overloading
    │   │   │   ├── RegistrationService.java  # Runtime Polymorphism validation
    │   │   │   └── DashboardService.java     # Dynamic metrics aggregation
    │   │   ├── controller/
    │   │   │   ├── StudentController.java    # REST API /api/students
    │   │   │   ├── CourseController.java     # REST API /api/courses
    │   │   │   ├── RegistrationController.java # REST API /api/registrations
    │   │   │   └── DashboardController.java  # REST API /api/dashboard/summary
    │   │   ├── exception/
    │   │   │   ├── StudentManagementException.java # Exception hierarchy root
    │   │   │   ├── DuplicateStudentException.java
    │   │   │   ├── StudentNotFoundException.java
    │   │   │   ├── CourseNotFoundException.java
    │   │   │   ├── DuplicateCourseException.java
    │   │   │   ├── CourseCapacityExceededException.java
    │   │   │   ├── MaxEnrollmentLimitExceededException.java
    │   │   │   ├── ActiveEnrollmentException.java
    │   │   │   └── GlobalExceptionHandler.java # @RestControllerAdvice translator
    │   │   ├── dto/
    │   │   │   ├── StudentRequest.java
    │   │   │   ├── StudentResponse.java
    │   │   │   ├── CourseRequest.java
    │   │   │   ├── RegistrationRequest.java
    │   │   │   ├── DashboardResponse.java
    │   │   │   └── ErrorResponse.java
    │   │   ├── adapter/
    │   │   │   └── LegacyStudentAdapter.java # Adapter Design Pattern
    │   │   └── config/
    │   │       └── AppConfiguration.java     # Singleton bean & CORS setup
    │   └── resources/
    │       ├── application.properties        # Port 8081 and storage config
    │       └── static/                       # Served directly by Spring Boot
    │           ├── index.html                # Executive single-page dashboard
    │           ├── css/style.css             # Midnight navy executive palette
    │           └── js/
    │               ├── api.js                # Centralized fetch API service
    │               └── app.js                # Interactive UI coordinator
    └── test/java/com/college/sms/
        ├── StudentServiceTest.java           # Unit tests for students & validation
        ├── CourseServiceTest.java            # Unit tests for courses & overloading
        ├── PolymorphismAndEnrollmentTest.java # Tests for dynamic dispatch & limits
        ├── FilePersistenceTest.java          # Tests for restart safety & adapter
        └── StudentManagementIntegrationTest.java # Spring Boot REST integration tests
```

---

## 🚀 How to Run the Application

### 1. Compile and Run Tests
```bash
# Compile the backend
mvn clean compile

# Execute all 29 automated JUnit tests
mvn test
```

### 2. Start the Spring Boot Server
```bash
mvn spring-boot:run
```

### 3. Open in Your Web Browser
Navigate to:
```text
http://localhost:8081
```
The embedded Tomcat web server serves both the Spring Boot REST APIs and the static HTML5 dashboard simultaneously on port 8081.

---

## 📚 Key OOP Concepts Implemented

| Unit | Syllabus Concept | Where It Is Implemented |
|---|---|---|
| **Unit I** | Classes, Objects & Encapsulation | [`Student.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Student.java), [`Course.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java) with `private` fields and validated mutators |
| **Unit I** | Data Abstraction | `public abstract int getMaxCourses();` in [`Student.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Student.java) |
| **Unit II** | Constructor Overloading | Full constructor and default-capacity constructor in [`Course.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java) |
| **Unit II** | Method Overloading (Compile-Time) | Overloaded `searchStudents` in [`StudentService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/StudentService.java) |
| **Unit II** | Composition & Association | `Student` composes [`ContactDetails`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/ContactDetails.java); [`Registration`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Registration.java) associates Student and Course |
| **Unit III** | Inheritance | [`UndergraduateStudent`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/UndergraduateStudent.java) and [`PostgraduateStudent`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/PostgraduateStudent.java) `extends Student` |
| **Unit III** | Runtime Polymorphism (Dynamic Binding) | [`RegistrationService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/RegistrationService.java) calls `student.getMaxCourses()` dynamically returning 6 for UG and 4 for PG |
| **Unit III** | Interfaces & Generic Contracts | Generic [`Repository<T, ID>`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/Repository.java) interface |
| **Unit IV** | User-Defined Exceptions | Custom exceptions for missing, duplicate, full capacity, or max limit conditions |
| **Unit IV** | Centralized Exception Handling | [`GlobalExceptionHandler.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/exception/GlobalExceptionHandler.java) mapping exceptions to HTTP 400, 404, 409 |
| **Unit V** | File I/O Persistence | Character streams (`BufferedReader`/`BufferedWriter`) with atomic file swapping in [`CsvHelper.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/CsvHelper.java) |
| **Unit V** | Adapter Design Pattern | [`LegacyStudentAdapter.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/adapter/LegacyStudentAdapter.java) adapting old CSV dictionaries to `Student` objects |
| **Unit V** | Iterator Design Pattern | `Iterator<Registration>` loops in [`FileRegistrationRepository.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/FileRegistrationRepository.java) |
| **Unit V** | Singleton Design Pattern | Spring-managed singleton beans configured in [`AppConfiguration.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/config/AppConfiguration.java) |

---

## 📡 REST API Summary

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `GET` | `/api/students` | List all students (optional `?search=` or `?department=&type=`) | `200 OK` |
| `GET` | `/api/students/{id}` | Get student by roll number | `200 OK`, `404 Not Found` |
| `POST` | `/api/students` | Create student | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `PUT` | `/api/students/{id}` | Update student | `200 OK`, `404 Not Found`, `409 Conflict` |
| `DELETE` | `/api/students/{id}` | Delete student (blocked if active enrollments exist) | `204 No Content`, `409 Conflict` |
| `GET` | `/api/courses` | List all courses (optional `?search=`) | `200 OK` |
| `GET` | `/api/courses/{id}` | Get course by course code | `200 OK`, `404 Not Found` |
| `POST` | `/api/courses` | Create course | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `PUT` | `/api/courses/{id}` | Update course | `200 OK`, `404 Not Found`, `409 Conflict` |
| `DELETE` | `/api/courses/{id}` | Delete course (blocked if active enrollments exist) | `204 No Content`, `409 Conflict` |
| `GET` | `/api/registrations` | List all registrations | `200 OK` |
| `POST` | `/api/registrations` | Register student into course (enforces polymorphic limits) | `201 Created`, `409 Conflict` |
| `DELETE` | `/api/registrations/{id}` | Deregister enrollment by ID | `204 No Content`, `404 Not Found` |
| `GET` | `/api/dashboard/summary` | Dynamic real-time metrics summary | `200 OK` |

---

## 🧪 Automated Test Verification

All 29 tests pass with **0 failures and 0 errors**:

```text
[INFO] Results:
[INFO] Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

For complete viva preparation, consult [`PROJECT_EXPLANATION.md`](file:///Users/mrugmaythere/work/OOP%20ag/PROJECT_EXPLANATION.md) and [`OOP_SYLLABUS_MAPPING.md`](file:///Users/mrugmaythere/work/OOP%20ag/OOP_SYLLABUS_MAPPING.md).
