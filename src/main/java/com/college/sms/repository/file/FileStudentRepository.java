package com.college.sms.repository.file;

import com.college.sms.model.PostgraduateStudent;
import com.college.sms.model.Student;
import com.college.sms.model.UndergraduateStudent;
import com.college.sms.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ==============================================================================
 * FileStudentRepository (OOP Concept: File I/O, Generics & Collections)
 * ==============================================================================
 * Implements StudentRepository using local CSV file persistence.
 * Demonstrates:
 * 1. File Handling: Reads and writes students.csv via buffered streams.
 * 2. Collections: Uses CopyOnWriteArrayList and ConcurrentHashMap for safe in-memory cache.
 * 3. Polymorphic Deserialization: Reconstructs UndergraduateStudent or PostgraduateStudent
 *    based on the stored studentType column.
 * 4. Persistence Integrity: Changes immediately persist to disk across restarts.
 * ==============================================================================
 */
@Repository
public class FileStudentRepository implements StudentRepository {

    private static final String CSV_HEADER = "id,studentId,fullName,email,phoneNumber,department,semester,studentType";

    private final Path filePath;
    private final List<Student> studentList = new CopyOnWriteArrayList<>();
    private final Map<String, Student> studentMap = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(100);

    @org.springframework.beans.factory.annotation.Autowired
    public FileStudentRepository(@Value("${app.data.dir:data}") String dataDir) {
        this.filePath = Paths.get(dataDir, "students.csv");
    }

    /**
     * Testing constructor allowing custom file path.
     */
    public FileStudentRepository(Path customFilePath) {
        this.filePath = customFilePath;
    }

    @PostConstruct
    public synchronized void init() {
        try {
            if (!Files.exists(filePath)) {
                seedInitialData();
            } else {
                loadFromFile();
                if (studentList.isEmpty()) {
                    seedInitialData();
                }
            }
        } catch (IOException e) {
            System.err.println("[FileStudentRepository] Warning: Could not initialize student CSV: " + e.getMessage());
        }
    }

    private synchronized void loadFromFile() throws IOException {
        studentList.clear();
        studentMap.clear();

        List<String> lines = CsvHelper.readLines(filePath);
        boolean firstLine = true;
        long maxId = 0;

        for (String line : lines) {
            if (firstLine) {
                firstLine = false;
                if (line.startsWith("id,")) {
                    continue; // Skip header
                }
            }

            List<String> tokens = CsvHelper.parseLine(line);
            if (tokens.size() >= 8) {
                try {
                    Long id = Long.parseLong(tokens.get(0));
                    String studentId = tokens.get(1);
                    String fullName = tokens.get(2);
                    String email = tokens.get(3);
                    String phoneNumber = tokens.get(4);
                    String department = tokens.get(5);
                    int semester = Integer.parseInt(tokens.get(6));
                    String studentType = tokens.get(7);

                    Student student;
                    if ("Postgraduate".equalsIgnoreCase(studentType)) {
                        student = new PostgraduateStudent(id, studentId, fullName, email, phoneNumber, department, semester);
                    } else {
                        student = new UndergraduateStudent(id, studentId, fullName, email, phoneNumber, department, semester);
                    }

                    studentList.add(student);
                    studentMap.put(studentId.toUpperCase(), student);
                    if (id > maxId) maxId = id;
                } catch (Exception ex) {
                    System.err.println("[FileStudentRepository] Skipped corrupted student row: " + line);
                }
            }
        }
        idSequence.set(Math.max(maxId + 1, 100));
    }

    private synchronized void flushToFile() {
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        for (Student s : studentList) {
            List<String> tokens = Arrays.asList(
                    String.valueOf(s.getId()),
                    s.getStudentId(),
                    s.getFullName(),
                    s.getEmail(),
                    s.getPhoneNumber(),
                    s.getDepartment(),
                    String.valueOf(s.getSemester()),
                    s.getStudentType()
            );
            lines.add(CsvHelper.formatLine(tokens));
        }

        try {
            CsvHelper.writeLinesAtomically(filePath, lines);
        } catch (IOException e) {
            System.err.println("[FileStudentRepository] Failed to write student CSV: " + e.getMessage());
        }
    }

    private synchronized void seedInitialData() throws IOException {
        studentList.clear();
        studentMap.clear();

        List<Student> initialSeeds = List.of(
                new UndergraduateStudent(1L, "STU101", "Rahul Sharma", "rahul.sharma@college.edu", "9876543210", "Computer Science", 4),
                new UndergraduateStudent(2L, "STU102", "Priya Patel", "priya.patel@college.edu", "9823456781", "Information Technology", 6),
                new PostgraduateStudent(3L, "STU103", "Aman Verma", "aman.verma@college.edu", "9123456789", "Computer Science", 2),
                new UndergraduateStudent(4L, "STU104", "Sneha Nair", "sneha.nair@college.edu", "9786543210", "Electronics & Communication", 4)
        );

        for (Student s : initialSeeds) {
            studentList.add(s);
            studentMap.put(s.getStudentId().toUpperCase(), s);
        }
        idSequence.set(5L);
        flushToFile();
    }

    // --------------------------------------------------------------------------
    // Repository Interface Implementation
    // --------------------------------------------------------------------------

    @Override
    public List<Student> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(studentList));
    }

    @Override
    public Optional<Student> findById(String studentId) {
        return findByStudentId(studentId);
    }

    @Override
    public Optional<Student> findByStudentId(String studentId) {
        if (studentId == null) return Optional.empty();
        return Optional.ofNullable(studentMap.get(studentId.trim().toUpperCase()));
    }

    @Override
    public boolean existsById(String studentId) {
        return existsByStudentId(studentId);
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        if (studentId == null) return false;
        return studentMap.containsKey(studentId.trim().toUpperCase());
    }

    @Override
    public synchronized Student save(Student student) {
        if (student.getId() == null) {
            student.setId(idSequence.getAndIncrement());
        }

        String key = student.getStudentId().toUpperCase();
        // Remove existing from list if update
        studentList.removeIf(s -> s.getStudentId().equalsIgnoreCase(student.getStudentId()));
        studentList.add(student);
        studentMap.put(key, student);

        flushToFile();
        return student;
    }

    @Override
    public synchronized void deleteById(String studentId) {
        if (studentId == null) return;
        String key = studentId.trim().toUpperCase();
        studentMap.remove(key);
        studentList.removeIf(s -> s.getStudentId().equalsIgnoreCase(studentId));
        flushToFile();
    }

    @Override
    public long count() {
        return studentList.size();
    }

    @Override
    public List<Student> findByDepartment(String department) {
        if (department == null) return Collections.emptyList();
        List<Student> results = new ArrayList<>();
        for (Student s : studentList) {
            if (s.getDepartment().equalsIgnoreCase(department.trim())) {
                results.add(s);
            }
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public List<Student> findByStudentType(String studentType) {
        if (studentType == null) return Collections.emptyList();
        List<Student> results = new ArrayList<>();
        for (Student s : studentList) {
            if (s.getStudentType().equalsIgnoreCase(studentType.trim())) {
                results.add(s);
            }
        }
        return Collections.unmodifiableList(results);
    }
}
