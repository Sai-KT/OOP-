# Student Management System (College OOP Mini-Project)

A simple, clean, and ultra-premium frontend for a **Student Management System** built with **HTML5, CSS3, and Vanilla JavaScript**, designed to pair with a **Java Spring Boot** backend.

---

## 📁 Project Structure

```text
OOP ag/
├── README.md                 # Complete documentation & API contract reference
└── src/
    └── main/
        └── resources/
            └── static/
                ├── index.html        # Main SPA interface & layout
                ├── css/
                │   └── style.css     # Ultra-premium theme styling & dark mode tokens
                └── js/
                    ├── api.js        # Centralized fetch() API service & mock mode
                    └── app.js        # Dynamic DOM rendering, analytics, CSV exports & CRUD
```

---

## 🚀 How to Run and Test

### Option 1: Standalone Browser Testing (Demo / Mock Mode)
You can test the entire frontend directly without starting the backend:
1. Double-click `src/main/resources/static/index.html` to open it in Chrome, Edge, Safari, or Firefox.
2. Or serve it using Python's built-in HTTP server:
   ```bash
   python3 -m http.server 8080 --directory src/main/resources/static
   ```
3. Open `http://localhost:8080` in your web browser.
4. **Demo / Mock Mode** is active by default with initial sample data. You can add, edit, search, enroll, and delete records seamlessly.

### Option 2: Running with Java Spring Boot Backend
1. Place the frontend files inside `src/main/resources/static/` of your Spring Boot project (already configured).
2. Start your Spring Boot application (defaulting to `http://localhost:8080`).
3. Open `http://localhost:8080` in your browser.
4. Flip the **Backend Connection** toggle switch in the left sidebar to **Live Backend**. The app will immediately communicate with your Spring Boot REST controllers.

---

## 💎 Premium UI Highlights

1. **Dashboard & Analytics:**
   - **Hero Welcome Card:** Administrative session overview and quick action shortcuts.
   - **Real-Time Summary Metrics:** Dynamically calculated **Total Students**, **Total Courses**, and **Total Enrollments** with trend indicators.
   - **Department Distribution Bar:** Multi-segment colored progress visualizer displaying student enrollment share across Computer Science, IT, EC, Mechanical, and Civil branches.
   - **Seat Occupancy Meter:** Live course capacity tracker displaying percentage of total available college seats filled.
   - **Recently Added Students:** Live table with custom avatar badges.

2. **Student Directory:**
   - **Avatars & Visual Hierarchy:** Deterministic initials avatar circles with tailored color palettes.
   - **Segmented Filter Tabs:** Quick toggling between `All Students`, `Undergraduate`, and `Postgraduate`.
   - **Search & Clear:** Real-time search across ID, Name, and Email with an instant clear button (`×`).
   - **Export to CSV:** Direct client-side CSV download (`students_directory.csv`).
   - **Add/Edit Modal:** Native `<dialog>` modal with validation for ID, Name, Email format, Phone digits, and Semester (1–8).

3. **Course Management:**
   - **Capacity Visualizer:** Interactive seat fill progress bar inside each table row (turns amber at 75% capacity and red when 100% full).
   - **Credits Badges:** Modern monospace badges for Course Codes and Credits.
   - **Export to CSV:** Instant download of course catalog data (`courses_catalog.csv`).

4. **Course Enrollment:**
   - **Smart Enrollment Picker:** Interactive dropdowns indicating live available seat counts (`X seats left` or `FULL`).
   - **Business Rules Enforcement:** Prevents duplicate enrollments and displays capacity warnings.
   - **Export to CSV:** Download enrollment records (`course_enrollments.csv`).

5. **Design & Theming:**
   - **Dark Mode & Light Mode:** Toggleable via the top header sun/moon button with persistent preference storage in `localStorage`.
   - **Typography:** Modern Google Fonts (`Plus Jakarta Sans` for UI, `JetBrains Mono` for alphanumeric IDs).
   - **Micro-Animations:** Pulsing connection indicator dot, card hover lifts, and smooth dialog entry transitions with `@starting-style`.

---

## 📡 Spring Boot REST API Contracts

The frontend expects the following endpoints from your Spring Boot controllers:

### 1. Students (`/api/students`)

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| `GET` | `/api/students` | Get all students | None | `List<Student>` (200 OK) |
| `GET` | `/api/students/{id}` | Get student by ID | None | `Student` (200 OK) |
| `POST` | `/api/students` | Create new student | Student JSON | Created `Student` (201 Created) |
| `PUT` | `/api/students/{id}` | Update student | Student JSON | Updated `Student` (200 OK) |
| `DELETE` | `/api/students/{id}` | Delete student | None | 204 No Content |

#### Student JSON Format
```json
{
  "studentId": "STU101",
  "fullName": "Rahul Sharma",
  "email": "rahul.sharma@college.edu",
  "phoneNumber": "9876543210",
  "department": "Computer Science",
  "semester": 4,
  "studentType": "Undergraduate"
}
```

---

### 2. Courses (`/api/courses`)

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| `GET` | `/api/courses` | Get all courses | None | `List<Course>` (200 OK) |
| `GET` | `/api/courses/{id}` | Get course by ID/code | None | `Course` (200 OK) |
| `POST` | `/api/courses` | Create new course | Course JSON | Created `Course` (201 Created) |
| `PUT` | `/api/courses/{id}` | Update course | Course JSON | Updated `Course` (200 OK) |
| `DELETE` | `/api/courses/{id}` | Delete course | None | 204 No Content |

#### Course JSON Format
```json
{
  "courseCode": "CS201",
  "courseName": "Data Structures & Algorithms",
  "credits": 4,
  "maxCapacity": 40
}
```

---

### 3. Enrollments / Registrations (`/api/registrations`)

| Method | Endpoint | Description | Request Body | Response |
|---|---|---|---|---|
| `GET` | `/api/registrations` | Get all enrollments | None | `List<Registration>` (200 OK) |
| `POST` | `/api/registrations` | Enroll student | Enrollment JSON | Created `Registration` (201 Created) |
| `DELETE` | `/api/registrations/{id}` | Remove enrollment | None | 204 No Content |

#### Enrollment POST Payload
```json
{
  "studentId": "STU101",
  "courseCode": "CS201"
}
```

#### Enrollment GET Response Item
```json
{
  "id": 1,
  "studentId": "STU101",
  "studentName": "Rahul Sharma",
  "courseCode": "CS201",
  "courseName": "Data Structures & Algorithms"
}
```

---

## 🎓 Suggested Java OOP Architecture (For College Viva)

When presenting in your college OOP viva:

1. **Encapsulation:**
   - `Student` class encapsulates private fields (`studentId`, `fullName`, `email`, `department`, `semester`) with public getters/setters and constructor validation.
   - `Course` class encapsulates private fields (`courseCode`, `courseName`, `credits`, `maxCapacity`).

2. **Inheritance & Polymorphism:**
   - Base `Person` or `User` class inherited by `Student` (or base `Student` with derived `UndergraduateStudent` and `PostgraduateStudent` subclasses having polymorphic credit limits).

3. **Association / Aggregation:**
   - `Registration` / `Enrollment` models a Many-to-Many relationship linking `Student` and `Course`.

4. **Exception Handling:**
   - Course capacity check: throws `CourseCapacityExceededException` (returns HTTP 400).
   - Duplicate enrollment check: throws `DuplicateEnrollmentException` (returns HTTP 409).
   - Student not found: throws `StudentNotFoundException` (returns HTTP 404).
