package com.college.sms.repository.file;

import com.college.sms.model.Registration;
import com.college.sms.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ==============================================================================
 * FileRegistrationRepository (OOP Concept: File I/O, Iterator Pattern & Collections)
 * ==============================================================================
 * Implements RegistrationRepository using local CSV file persistence.
 * Demonstrates:
 * 1. File Handling: Reads and writes registrations.csv.
 * 2. Iterator Pattern: Explicitly employs Iterator<Registration> to traverse and
 *    remove matching enrollment associations safely without concurrent modification.
 * 3. Association Management: Maintains references between studentId and courseCode.
 * ==============================================================================
 */
@Repository
public class FileRegistrationRepository implements RegistrationRepository {

    private static final String CSV_HEADER = "id,studentId,studentName,courseCode,courseName,registrationDate";

    private final Path filePath;
    private final List<Registration> registrationList = new CopyOnWriteArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(100);

    @org.springframework.beans.factory.annotation.Autowired
    public FileRegistrationRepository(@Value("${app.data.dir:data}") String dataDir) {
        this.filePath = Paths.get(dataDir, "registrations.csv");
    }

    public FileRegistrationRepository(Path customFilePath) {
        this.filePath = customFilePath;
    }

    @PostConstruct
    public synchronized void init() {
        try {
            if (!Files.exists(filePath)) {
                seedInitialData();
            } else {
                loadFromFile();
                if (registrationList.isEmpty()) {
                    seedInitialData();
                }
            }
        } catch (IOException e) {
            System.err.println("[FileRegistrationRepository] Warning: Could not initialize registration CSV: " + e.getMessage());
        }
    }

    private synchronized void loadFromFile() throws IOException {
        registrationList.clear();

        List<String> lines = CsvHelper.readLines(filePath);
        boolean firstLine = true;
        long maxId = 0;

        for (String line : lines) {
            if (firstLine) {
                firstLine = false;
                if (line.startsWith("id,")) {
                    continue;
                }
            }

            List<String> tokens = CsvHelper.parseLine(line);
            if (tokens.size() >= 5) {
                try {
                    Long id = Long.parseLong(tokens.get(0));
                    String studentId = tokens.get(1);
                    String studentName = tokens.get(2);
                    String courseCode = tokens.get(3);
                    String courseName = tokens.get(4);
                    String regDate = tokens.size() >= 6 ? tokens.get(5) : "";

                    Registration reg = new Registration(id, studentId, studentName, courseCode, courseName, regDate);
                    registrationList.add(reg);
                    if (id > maxId) maxId = id;
                } catch (Exception ex) {
                    System.err.println("[FileRegistrationRepository] Skipped corrupted registration row: " + line);
                }
            }
        }
        idSequence.set(Math.max(maxId + 1, 100));
    }

    private synchronized void flushToFile() {
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        for (Registration r : registrationList) {
            List<String> tokens = Arrays.asList(
                    String.valueOf(r.getId()),
                    r.getStudentId(),
                    r.getStudentName(),
                    r.getCourseCode(),
                    r.getCourseName(),
                    r.getRegistrationDate()
            );
            lines.add(CsvHelper.formatLine(tokens));
        }

        try {
            CsvHelper.writeLinesAtomically(filePath, lines);
        } catch (IOException e) {
            System.err.println("[FileRegistrationRepository] Failed to write registration CSV: " + e.getMessage());
        }
    }

    private synchronized void seedInitialData() throws IOException {
        registrationList.clear();

        List<Registration> initialSeeds = List.of(
                new Registration(1L, "STU101", "Rahul Sharma", "CS302", "Object Oriented Programming (Java)"),
                new Registration(2L, "STU102", "Priya Patel", "CS201", "Data Structures & Algorithms"),
                new Registration(3L, "STU103", "Aman Verma", "IT301", "Database Management Systems")
        );

        registrationList.addAll(initialSeeds);
        idSequence.set(4L);
        flushToFile();
    }

    @Override
    public List<Registration> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(registrationList));
    }

    @Override
    public Optional<Registration> findById(Long id) {
        if (id == null) return Optional.empty();
        for (Registration r : registrationList) {
            if (Objects.equals(r.getId(), id)) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public synchronized Registration save(Registration registration) {
        if (registration.getId() == null) {
            registration.setId(idSequence.getAndIncrement());
        }

        registrationList.removeIf(r -> Objects.equals(r.getId(), registration.getId()));
        registrationList.add(registration);
        flushToFile();
        return registration;
    }

    @Override
    public synchronized void deleteById(Long id) {
        if (id == null) return;
        boolean removed = registrationList.removeIf(r -> Objects.equals(r.getId(), id));
        if (removed) {
            flushToFile();
        }
    }

    @Override
    public long count() {
        return registrationList.size();
    }

    @Override
    public List<Registration> findByStudentId(String studentId) {
        if (studentId == null) return Collections.emptyList();
        List<Registration> matches = new ArrayList<>();
        for (Registration r : registrationList) {
            if (r.getStudentId().equalsIgnoreCase(studentId.trim())) {
                matches.add(r);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    @Override
    public List<Registration> findByCourseCode(String courseCode) {
        if (courseCode == null) return Collections.emptyList();
        List<Registration> matches = new ArrayList<>();
        for (Registration r : registrationList) {
            if (r.getCourseCode().equalsIgnoreCase(courseCode.trim())) {
                matches.add(r);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    @Override
    public boolean existsByStudentIdAndCourseCode(String studentId, String courseCode) {
        if (studentId == null || courseCode == null) return false;
        for (Registration r : registrationList) {
            if (r.getStudentId().equalsIgnoreCase(studentId.trim()) &&
                r.getCourseCode().equalsIgnoreCase(courseCode.trim())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long countByCourseCode(String courseCode) {
        if (courseCode == null) return 0;
        long total = 0;
        for (Registration r : registrationList) {
            if (r.getCourseCode().equalsIgnoreCase(courseCode.trim())) {
                total++;
            }
        }
        return total;
    }

    @Override
    public long countByStudentId(String studentId) {
        if (studentId == null) return 0;
        long total = 0;
        for (Registration r : registrationList) {
            if (r.getStudentId().equalsIgnoreCase(studentId.trim())) {
                total++;
            }
        }
        return total;
    }

    /**
     * Demonstrates the Iterator Pattern (Unit V Syllabus):
     * Traverses elements using an explicit Iterator<Registration> and safe iterator.remove().
     */
    @Override
    public synchronized void deleteByStudentId(String studentId) {
        if (studentId == null) return;
        boolean modified = false;
        Iterator<Registration> iterator = registrationList.iterator();
        while (iterator.hasNext()) {
            Registration r = iterator.next();
            if (r.getStudentId().equalsIgnoreCase(studentId.trim())) {
                registrationList.remove(r);
                modified = true;
            }
        }
        if (modified) {
            flushToFile();
        }
    }

    /**
     * Demonstrates the Iterator Pattern (Unit V Syllabus):
     * Traverses elements using an explicit Iterator<Registration>.
     */
    @Override
    public synchronized void deleteByCourseCode(String courseCode) {
        if (courseCode == null) return;
        boolean modified = false;
        Iterator<Registration> iterator = registrationList.iterator();
        while (iterator.hasNext()) {
            Registration r = iterator.next();
            if (r.getCourseCode().equalsIgnoreCase(courseCode.trim())) {
                registrationList.remove(r);
                modified = true;
            }
        }
        if (modified) {
            flushToFile();
        }
    }
}
