# 📚 Object-Oriented Programming (OOP) Syllabus Mapping

This document provides a comprehensive mapping between the **University Object-Oriented Programming (OOP) Syllabus (Units I through V)** and the Java source code implemented in the **Student Management System** mini-project.

Use this document to prepare for and confidently answer questions during your **project viva and practical evaluation**.

---

## 📌 Master Syllabus Mapping Table

| Unit | Syllabus OOP Concept | Java Class / Interface | Implementation / Method | Technical Explanation | How to Demonstrate in Project Viva |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **I** | **Classes & Objects** | [`Student`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Student.java), [`Course`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java) | Class definitions & `new` instantiation | Models real-world entities with encapsulated state and behavior rather than procedural global arrays. | Show student form submission creating a `Student` object in [`StudentController.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/controller/StudentController.java). |
| **I** | **Encapsulation** | [`Student`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Student.java), [`Course`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java) | `private` variables with validated getters & setters | Protects object integrity; prevents negative credits or invalid semesters. | Show validation in `Course.setCredits(int)` rejecting `credits <= 0`. |
| **I** | **Data Abstraction** | [`Student.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Student.java) | `public abstract int getMaxCourses();` | Exposes high-level contract while hiding student type specific limits inside derived classes. | Show how `RegistrationService` invokes `getMaxCourses()` without needing internal student fields. |
| **I** | **Information Hiding** | [`StudentService`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/StudentService.java) | `Collections.unmodifiableList(...)` | Internal collections cannot be directly tampered with by external callers. | Show `FileStudentRepository.findAll()` returning an unmodifiable list copy. |
| **II** | **Access Control** | All Models & Services | `private`, `protected`, `public` | Controls scope: state is private, helper methods private/protected, APIs public. | Point to private variables in `Student` and protected constructor. |
| **II** | **Constructor Overloading** | [`Course.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java) | `Course(...)` (all fields) & `Course(...)` (defaults capacity to 40) | Multiple constructors with different parameter signatures (`this(...)` chaining). | Point to overloaded constructors in `Course.java` lines 47–65. |
| **II** | **Method Overloading (Compile-Time Polymorphism)** | [`StudentService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/StudentService.java), [`CourseService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/CourseService.java) | `searchStudents(String query)` vs `searchStudents(String dept, String type)` | Same method name with different parameter signatures resolved at compile time. | Show both `searchStudents` methods in `StudentService.java`. |
| **II** | **Static Members** | [`Course.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java) | `public static final int DEFAULT_CAPACITY = 40;` | Class-level constants shared across all instances without requiring object creation. | Explain why default capacity is `static final` (shared constant). |
| **II** | **Association** | [`Registration.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Registration.java) | Connects `Student` (via `studentId`) and `Course` (via `courseCode`) | Structural relationship representing a student enrolled in a course. | Show `Registration` holding foreign references to Student and Course. |
| **II** | **Composition** | [`Student.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Student.java), [`ContactDetails.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/ContactDetails.java) | `Student` owns `ContactDetails` | Strong relationship: Contact details cannot exist independently without the student. | Show `student.getContactDetails()` and constructor composition. |
| **II** | **Aggregation** | [`RegistrationService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/RegistrationService.java) | Collection of independent `Student` and `Course` objects | Has-A relationship where components can exist independently of the container. | Explain that Students and Courses exist independently of registrations. |
| **III** | **Abstract Class & Hierarchy** | [`Student.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Student.java) | `public abstract class Student` | Cannot be instantiated directly; provides common state & abstract behavior. | Try to do `new Student()` in test and explain why compiler blocks it. |
| **III** | **Single Inheritance** | [`UndergraduateStudent`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/UndergraduateStudent.java), [`PostgraduateStudent`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/PostgraduateStudent.java) | `extends Student` | Derived subclasses inherit all attributes and behaviors from base `Student`. | Show `public class UndergraduateStudent extends Student`. |
| **III** | **Constructor Chaining (`super`)** | Derived Student classes | `super(id, studentId, fullName, ...)` | Calls parent constructor to initialize inherited base properties. | Show `super(...)` call on first line of `UndergraduateStudent` constructor. |
| **III** | **Method Overriding** | Subclasses of `Student` | `@Override public int getMaxCourses()` | Derived class replaces parent method implementation with specialized logic. | Show `getMaxCourses()` returning 6 in UG and 4 in PG. |
| **III** | **Runtime Polymorphism (Dynamic Binding)** | [`RegistrationService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/RegistrationService.java) | `student.getMaxCourses()` | Method call resolution occurs dynamically at runtime based on the actual object type. | **Critical Viva Point:** Show line 76 in `RegistrationService.java` where `student.getMaxCourses()` enforces 6 for UG and 4 for PG without `if (student instanceof...)`! |
| **III** | **Interfaces** | [`Repository<T, ID>`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/Repository.java) | Generic CRUD interface | Defines pure abstraction and behavioral contract decoupled from persistence. | Show `FileStudentRepository implements StudentRepository`. |
| **IV** | **User-Defined Exceptions** | [`exception/`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/exception) | `DuplicateStudentException`, `CourseCapacityExceededException`, `MaxEnrollmentLimitExceededException` | Custom application exceptions conveying domain-specific failure reasons. | Trigger duplicate roll number error and show toast notification. |
| **IV** | **Exception Handling** | [`GlobalExceptionHandler.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/exception/GlobalExceptionHandler.java) | `try-catch`, `throw`, `@ExceptionHandler` | Robust error propagation; converts Java exceptions to HTTP 400/404/409 responses. | Show how custom exceptions map cleanly to HTTP status codes. |
| **IV** | **Generic Programming** | [`Repository<T, ID>`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/Repository.java) | Type parameters `<T, ID>` | Compile-time type safety; eliminates raw types and unsafe casting. | Show `Repository<Student, String>` vs `Repository<Registration, Long>`. |
| **IV** | **Java Collections Framework** | Repositories & Services | `List<Student>`, `ArrayList`, `Map<String, Student>`, `Set<String>` | Appropriate data structures for in-memory caching and fast lookups. | Explain why `ConcurrentHashMap` was chosen for O(1) roll number lookups. |
| **V** | **File Handling & Streams** | [`CsvHelper.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/CsvHelper.java) | `BufferedReader`, `BufferedWriter`, Character Streams | Local CSV persistence without heavy SQL databases; UTF-8 encoded file reading/writing. | Open `data/students.csv` in terminal to show saved records. |
| **V** | **File Safety & Atomic Writes** | [`CsvHelper.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/CsvHelper.java) | Temporary swap file & `StandardCopyOption.ATOMIC_MOVE` | Protects data integrity against power loss or crashes during writes. | Show `writeLinesAtomically` in `CsvHelper.java`. |
| **V** | **Singleton Pattern** | [`AppConfiguration.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/config/AppConfiguration.java) | Spring-managed singleton beans (`@Service`, `@Repository`) | Exactly one instance shared application-wide, preventing redundant memory usage. | Explain Spring's default singleton scope for services and repositories. |
| **V** | **Adapter Pattern** | [`LegacyStudentAdapter.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/adapter/LegacyStudentAdapter.java) | `adaptFromLegacyRecord(Map<String, String>)` | Converts incompatible legacy university CSV data to modern `Student` objects. | Show `FilePersistenceTest.testLegacyStudentAdapter()` unit test. |
| **V** | **Iterator Pattern** | [`FileRegistrationRepository.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/FileRegistrationRepository.java) | `Iterator<Registration> iterator = ...; while(iterator.hasNext())` | Sequential traversal of collections without exposing underlying data structure. | Show `deleteByStudentId` using `Iterator<Registration>` in `FileRegistrationRepository.java`. |

---

## 🔍 Detailed Unit-by-Unit Breakdown

### UNIT I — Introduction to OOP
- **Encapsulation**: State fields (`studentId`, `fullName`, `credits`, `maxCapacity`) are marked `private`. In [`Course.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java), setter validation guarantees that credits and capacity must be positive integers.
- **Data Abstraction**: The abstract class [`Student`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Student.java) defines `getMaxCourses()` and `getStudentType()`. The rest of the system deals with abstract students without needing to know concrete details.

### UNIT II — Classes, Objects & Relationships
- **Constructor Overloading**: [`Course`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Course.java) provides a full 5-argument constructor and a 4-argument constructor that chains using `this(id, code, name, credits, DEFAULT_CAPACITY)`.
- **Method Overloading**: [`StudentService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/StudentService.java) provides two overloaded `searchStudents` methods: one taking a single keyword `String query`, and another taking two filter criteria `String department, String studentType`.
- **Object Relationships**:
  - **Composition**: A `Student` contains a [`ContactDetails`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/ContactDetails.java) object. If the student is deleted, their contact details cease to exist.
  - **Association**: A [`Registration`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/Registration.java) associates an independent `Student` with an independent `Course`.
  - **Aggregation**: `StudentService` manages a collection of `Student` objects.

### UNIT III — Inheritance & Polymorphism
- **Class Hierarchy**: `Student` (abstract parent) -> [`UndergraduateStudent`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/UndergraduateStudent.java) and [`PostgraduateStudent`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/model/PostgraduateStudent.java) (derived children).
- **Constructor Chaining**: Subclass constructors invoke `super(id, studentId, fullName, email, phoneNumber, department, semester)`.
- **Runtime Polymorphism**: In [`RegistrationService.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/service/RegistrationService.java):
  ```java
  Student student = studentService.getStudentById(studentId);
  int allowedCourses = student.getMaxCourses(); // Polymorphic dispatch!
  ```
  At runtime, if `student` is an `UndergraduateStudent`, it executes `UndergraduateStudent.getMaxCourses()` and returns 6. If it is a `PostgraduateStudent`, it executes `PostgraduateStudent.getMaxCourses()` and returns 4.

### UNIT IV — Exception Handling & Generics
- **Custom Exceptions**:
  - `DuplicateStudentException` (Roll number conflicts)
  - `CourseNotFoundException` / `StudentNotFoundException` (Missing entities)
  - `CourseCapacityExceededException` (Seating full)
  - `MaxEnrollmentLimitExceededException` (Polymorphic limit reached)
  - `ActiveEnrollmentException` (Referential integrity protection)
- **Centralized Handler**: [`GlobalExceptionHandler.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/exception/GlobalExceptionHandler.java) uses `@RestControllerAdvice` to translate domain exceptions into structured HTTP JSON responses.
- **Generics**: [`Repository<T, ID>`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/Repository.java) provides compile-time type safety for domain repositories.

### UNIT V — File Handling & Design Patterns
- **CSV Persistence**: Separate files in `data/`: `students.csv`, `courses.csv`, and `registrations.csv`.
- **Safe File Writing**: [`CsvHelper.writeLinesAtomically`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/CsvHelper.java) writes to a temporary file before performing an atomic swap.
- **Adapter Pattern**: [`LegacyStudentAdapter.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/adapter/LegacyStudentAdapter.java) converts legacy university dictionaries (`roll_number`, `branch_name`, `program_type`) into standard `Student` objects.
- **Iterator Pattern**: [`FileRegistrationRepository.java`](file:///Users/mrugmaythere/work/OOP%20ag/src/main/java/com/college/sms/repository/file/FileRegistrationRepository.java) uses explicit `Iterator<Registration>` loops for safe removal during deregistration.
