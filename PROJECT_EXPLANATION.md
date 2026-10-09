# 🎓 Student Management System — Project Explanation & Viva Guide

A comprehensive, beginner-friendly guide to understanding, demonstrating, and defending the **Student Management System** in university viva evaluations.

---

## 📌 Table of Contents
1. [Project Overview](#1-project-overview)
2. [System Architecture](#2-system-architecture)
3. [Key Java Classes & Responsibilities](#3-key-java-classes--responsibilities)
4. [OOP Concepts Explained with Code Snippets](#4-oop-concepts-explained-with-code-snippets)
5. [Top 18 Viva Questions & Model Answers](#5-top-18-viva-questions--model-answers)
6. [Live Demonstration Script (Step-by-Step)](#6-live-demonstration-script-step-by-step)
7. [How to Compile, Run & Test the Application](#7-how-to-compile-run--test-the-application)

---

## 1. Project Overview

### What is this project?
The **Student Management System (SMS)** is an academic college administration portal designed for university record keeping. It allows college administrators and faculties to:
1. **Manage Students**: Enroll undergraduate and postgraduate students, maintain department details, and update academic semesters.
2. **Manage Courses**: Create academic courses, specify credit weights, and configure classroom capacities.
3. **Register Students into Courses**: Enroll students with automatic capacity checking, duplicate enrollment prevention, and student-type-based polymorphic course limit enforcement.
4. **Dashboard Analytics**: View live student counts, department distribution, and seat occupancy statistics.

### What problem does it solve?
In many colleges, student enrollments are managed either through manual spreadsheets or overly complicated enterprise systems. This project demonstrates how fundamental **Object-Oriented Programming (OOP) principles** in Java can create a clean, maintainable, reliable, and persistent application without needing heavy databases.

---

## 2. System Architecture

The backend follows a **clean 5-tier layered architecture**:

```
+-------------------------------------------------------------------------+
|                  Client Browser (HTML5 / CSS3 / Vanilla JS)             |
+-------------------------------------------------------------------------+
                                    | HTTP JSON Requests (port 8080)
                                    v
+-------------------------------------------------------------------------+
| Layer 1: REST Controllers (StudentController, CourseController, etc.)   |
| - Validates HTTP input, delegates to services, returns JSON DTOs        |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
| Layer 2: Service Layer (StudentService, CourseService, RegistrationSvc) |
| - Plain Java OOP business rules, validation, polymorphic limits         |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
| Layer 3: Generic Repositories (Repository<T, ID>, StudentRepository)   |
| - In-memory Collections (CopyOnWriteArrayList, ConcurrentHashMap)       |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
| Layer 4: File I/O Persistence (CsvHelper, FileStudentRepository)        |
| - Character streams (BufferedReader/BufferedWriter) & Atomic File Moves |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
| Layer 5: Data Storage (data/students.csv, courses.csv, registrations.csv)|
+-------------------------------------------------------------------------+
```

---

## 3. Key Java Classes & Responsibilities

| Package | Class / File | Primary Responsibility |
| :--- | :--- | :--- |
| `model` | [`Student.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Student.java) | Abstract root class representing student state, composition with `ContactDetails`, and abstract method `getMaxCourses()`. |
| `model` | [`UndergraduateStudent.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/UndergraduateStudent.java) | Derived class for Bachelor's students; overrides `getMaxCourses()` returning 6. |
| `model` | [`PostgraduateStudent.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/PostgraduateStudent.java) | Derived class for Master's/Ph.D. students; overrides `getMaxCourses()` returning 4. |
| `model` | [`ContactDetails.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/ContactDetails.java) | Represents student email and phone; demonstrates composition. |
| `model` | [`Course.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java) | Models academic courses; demonstrates constructor overloading and static constants. |
| `model` | [`Registration.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Registration.java) | Models the Association link between a Student and a Course. |
| `repository` | [`Repository.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/Repository.java) | Generic interface `<T, ID>` defining standard CRUD signatures. |
| `repository.file` | [`CsvHelper.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/CsvHelper.java) | Buffered character stream I/O and atomic temporary-file file replacement. |
| `service` | [`RegistrationService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/RegistrationService.java) | Enforces enrollment invariants and demonstrates Runtime Polymorphism via `student.getMaxCourses()`. |
| `exception` | [`GlobalExceptionHandler.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/exception/GlobalExceptionHandler.java) | Translates domain exceptions into clean JSON error responses. |
| `adapter` | [`LegacyStudentAdapter.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/adapter/LegacyStudentAdapter.java) | Demonstrates the Adapter Design Pattern converting old university CSV maps to `Student` objects. |

---

## 4. OOP Concepts Explained with Code Snippets

### 1. Encapsulation
State is kept `private`. Mutators enforce business validation:
```java
public void setCredits(int credits) {
    if (credits <= 0 || credits > 8) {
        throw new IllegalArgumentException("Credits must be between 1 and 8. Provided: " + credits);
    }
    this.credits = credits;
}
```

### 2. Constructor Overloading (Compile-Time Polymorphism)
[`Course.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java) provides multiple constructors:
```java
// Constructor 1: Fully parameterized
public Course(Long id, String courseCode, String courseName, int credits, int maxCapacity) { ... }

// Constructor 2: Overloaded, defaults capacity to 40 using constructor chaining 'this(...)'
public Course(Long id, String courseCode, String courseName, int credits) {
    this(id, courseCode, courseName, credits, DEFAULT_CAPACITY);
}
```

### 3. Inheritance & Constructor Chaining (`super`)
Subclasses inherit fields from the parent class and initialize them via `super(...)`:
```java
public class UndergraduateStudent extends Student {
    public UndergraduateStudent(Long id, String studentId, String fullName, String email,
                                String phoneNumber, String department, int semester) {
        super(id, studentId, fullName, email, phoneNumber, department, semester);
    }
}
```

### 4. Runtime Polymorphism (Dynamic Method Dispatch)
The registration engine works with the abstract type `Student` and dynamically resolves `getMaxCourses()`:
```java
// student is an abstract reference (Student student = ...)
int allowedCourses = student.getMaxCourses(); // Invokes overridden method at runtime!
if (currentEnrollments >= allowedCourses) {
    throw new MaxEnrollmentLimitExceededException(...);
}
```

### 5. Composition vs. Association
- **Composition**: In `Student.java`, `private ContactDetails contactDetails;` — the contact information cannot exist without the student.
- **Association**: In `Registration.java`, a registration holds references to an independently existing student and course.

### 6. Generic Programming
The repository contract uses type parameters `<T, ID>`:
```java
public interface Repository<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    T save(T entity);
    void deleteById(ID id);
}
```

---

## 5. Top 18 Viva Questions & Model Answers

### Q1: Why did you make the `Student` class abstract?
**Answer**: Because a student in our university must belong to a concrete degree program (either an Undergraduate or a Postgraduate student). Making `Student` abstract prevents direct instantiation of a generic student while allowing derived classes to inherit shared properties and enforce specialized course limits.

### Q2: How is Runtime Polymorphism demonstrated in your project?
**Answer**: In [`RegistrationService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/RegistrationService.java), the registration validation method receives an abstract `Student` reference and calls `student.getMaxCourses()`. At runtime, Java's dynamic binding mechanism executes `UndergraduateStudent.getMaxCourses()` (returning 6) or `PostgraduateStudent.getMaxCourses()` (returning 4) based on the actual object stored in memory, without requiring `instanceof` checks.

### Q3: What is the difference between Method Overloading and Method Overriding?
**Answer**:
- **Method Overloading (Compile-Time Polymorphism)** occurs when methods have the same name but different parameter lists within the same class (demonstrated in `StudentService.searchStudents`).
- **Method Overriding (Runtime Polymorphism)** occurs when a subclass re-implements a method inherited from its parent with the exact same signature (demonstrated by `getMaxCourses()` in `UndergraduateStudent` and `PostgraduateStudent`).

### Q4: Why use Composition for `ContactDetails` instead of Inheritance?
**Answer**: A Student is **NOT** a ContactDetail (inheritance requires an "Is-A" relationship). Rather, a Student **HAS** ContactDetails (composition). Contact details are part of a student's private state, so composition provides better encapsulation and modularity.

### Q5: How do you prevent duplicate student IDs?
**Answer**: Before persisting a student in `StudentService.createStudent`, we check `studentRepository.existsByStudentId(studentId)`. If a student already exists with that ID, we throw a custom `DuplicateStudentException`.

### Q6: What happens if an administrator tries to delete a student who is currently enrolled in a course?
**Answer**: The system enforces **referential integrity**. In `StudentService.deleteStudent`, we query `registrationRepository.countByStudentId(studentId)`. If active enrollments exist, the system blocks deletion by throwing `ActiveEnrollmentException` with a clear explanation message.

### Q7: How does your application persist data without a SQL database?
**Answer**: We implement local CSV persistence in `FileStudentRepository`, `FileCourseRepository`, and `FileRegistrationRepository` using Java character streams (`BufferedReader` and `BufferedWriter`) encoded in UTF-8. All records are saved to `data/students.csv`, `data/courses.csv`, and `data/registrations.csv`.

### Q8: What prevents CSV file corruption if a write fails halfway?
**Answer**: In [`CsvHelper.writeLinesAtomically`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/CsvHelper.java), data is first written to a temporary file (`.tmp`). Once the write finishes successfully, we perform an atomic file swap (`StandardCopyOption.ATOMIC_MOVE`), ensuring the original data is never corrupted if a failure occurs during writing.

### Q9: How are commas and quotes handled inside course names?
**Answer**: In `CsvHelper`, cell values containing commas or quotes are wrapped in double quotes, and internal quotes are escaped with `""` (`replace("\"", "\"\""`). The parser scans character-by-character and ignores commas that appear inside quotes.

### Q10: Where is the Adapter Pattern used?
**Answer**: In [`LegacyStudentAdapter.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/adapter/LegacyStudentAdapter.java). It acts as a wrapper converting legacy university dictionary columns (`roll_number`, `branch_name`, `program_type`) into the modern `Student` domain model.

### Q11: Where is the Iterator Pattern used?
**Answer**: In [`FileRegistrationRepository.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/FileRegistrationRepository.java). We use an explicit `Iterator<Registration>` loop to traverse and remove matching records during deregistration without triggering `ConcurrentModificationException`.

### Q12: Why are collection fields in models and repositories encapsulated?
**Answer**: To practice **Information Hiding**. If we returned internal collection references directly, external callers could alter our internal state without passing through our validation logic. We return unmodifiable copies (`Collections.unmodifiableList(...)`).

### Q13: What is the purpose of `GlobalExceptionHandler`?
**Answer**: It acts as a centralized exception translation layer using `@RestControllerAdvice`. Instead of littering every controller method with `try-catch`, it catches domain exceptions (`StudentNotFoundException`, `CourseCapacityExceededException`) and translates them into uniform HTTP JSON error responses with status codes 400, 404, or 409.

### Q14: What is the role of Spring Boot in this project?
**Answer**: Spring Boot is used strictly as a lightweight transport layer: handling HTTP REST requests, serving the frontend static files on port 8080, and managing dependency injection. All core business rules, entity models, validation, and file I/O are implemented in pure Java OOP.

### Q15: Why are `DEFAULT_CAPACITY` and `DEFAULT_CREDITS` declared as `public static final`?
**Answer**:
- `static`: Belongs to the `Course` class rather than any single object instance, saving memory.
- `final`: Makes the constant immutable so its value cannot be modified.

### Q16: How does the system handle course capacity limits?
**Answer**: Before registering a student in `RegistrationService`, we check:
```java
long enrolled = registrationRepository.countByCourseCode(courseCode);
if (enrolled >= course.getMaxCapacity()) {
    throw new CourseCapacityExceededException(...);
}
```
If the course is full, the registration is rejected.

### Q17: What collections did you use and why?
**Answer**:
- `CopyOnWriteArrayList`: For thread-safe sequential iteration of records.
- `ConcurrentHashMap`: For fast $O(1)$ lookups by student roll number and course code.
- `List<T>`: As the generic interface return type to decouple callers from specific collection implementations.

### Q18: What is Constructor Chaining?
**Answer**: Constructor chaining is calling one constructor from another within the same class using `this(...)` or from a derived class using `super(...)`. This reduces code duplication and ensures objects are consistently initialized.

---

## 6. Live Demonstration Script (Step-by-Step)

Follow these steps to demonstrate the application to your professor or examiner:

1. **Launch the Application**:
   - Run `mvn spring-boot:run` in the terminal.
   - Open [http://localhost:8080](http://localhost:8080) in your web browser.
   - Point out the **Executive Midnight Navy sidebar** and live connection indicator showing **"Live Server"**.

2. **Dashboard Overview**:
   - Show the dynamic dashboard metrics: Total Students, Total Courses, Total Enrollments, and Occupancy Rate.
   - Explain that these metrics are computed dynamically by `DashboardService` from actual Java records, not hardcoded.

3. **Add a Student**:
   - Navigate to the **Students** section or click **"+ Student"** in the sidebar.
   - Add an Undergraduate Student:
     - Roll Number: `STU201`
     - Name: `Aditi Rao`
     - Email: `aditi.rao@college.edu`
     - Phone: `9876501234`
     - Department: `Computer Science`
     - Semester: `4`
     - Type: `Undergraduate`
   - Click **Save Student**. Point out the success toast and the new record in the table.

4. **Demonstrate Duplicate Validation (Exception Handling)**:
   - Try to add another student with the same roll number `STU201`.
   - Point out the error toast: *"Student with ID 'STU201' already exists"*.
   - Explain that this was caught in Java by `DuplicateStudentException` and translated to HTTP 409 Conflict.

5. **Demonstrate Runtime Polymorphism (Course Limits)**:
   - Add a Postgraduate Student: `PG301`, `Karan Mehta`, `Postgraduate`.
   - Navigate to **Enrollments** and enroll `PG301` into 4 courses.
   - Try to enroll `PG301` into a 5th course:
   - The system displays: *"Student Karan Mehta (PG301) has reached the maximum enrollment limit of 4 courses allowed for Postgraduate students."*
   - Explain to the examiner: *"This limit of 4 was resolved at runtime by dynamic method dispatch calling `PostgraduateStudent.getMaxCourses()`."*

6. **Demonstrate Course Capacity Validation**:
   - Create a course with capacity = 1.
   - Enroll 1 student (succeeds).
   - Try enrolling a second student into the same course:
   - System displays: *"Course has reached its maximum capacity of 1 students."*

7. **Demonstrate Referential Integrity (Active Enrollment Protection)**:
   - Try to delete `STU101` from the Students table.
   - The deletion is blocked with a message: *"Cannot delete student: Student has active course enrollments. Remove enrollments first."*

8. **Demonstrate File Persistence Across Application Restart**:
   - Stop the backend in the terminal (`Ctrl + C`).
   - Show the files in the `data/` directory (`cat data/students.csv`). Point out that `STU201` was persisted.
   - Restart the server: `mvn spring-boot:run`.
   - Refresh the browser: all newly added students and registrations are still there!

---

## 7. How to Compile, Run & Test the Application

### Prerequisites:
- Java JDK 21 or higher installed (`java -version`)
- Apache Maven installed (`mvn -version`)

### Commands:
```bash
# 1. Compile the Java application and verify syntax
mvn clean compile

# 2. Run all 29 automated JUnit tests
mvn test

# 3. Package the application into an executable JAR
mvn package

# 4. Start the application
mvn spring-boot:run

# 5. Access the application in any web browser
http://localhost:8080
```
