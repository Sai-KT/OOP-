package com.college.sms.repository.file;

import com.college.sms.model.Course;
import com.college.sms.repository.CourseRepository;
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
 * FileCourseRepository (OOP Concept: File I/O, Generics & Collections)
 * ==============================================================================
 * Implements CourseRepository using local CSV file persistence.
 * Demonstrates:
 * 1. File Persistence: Reads and writes courses.csv.
 * 2. In-Memory Caching: Synchronizes collections with disk storage.
 * 3. Constructor Overloading in Action: Instantiates courses using overloaded constructor.
 * ==============================================================================
 */
@Repository
public class FileCourseRepository implements CourseRepository {

    private static final String CSV_HEADER = "id,courseCode,courseName,credits,maxCapacity";

    private final Path filePath;
    private final List<Course> courseList = new CopyOnWriteArrayList<>();
    private final Map<String, Course> courseMap = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(100);

    @org.springframework.beans.factory.annotation.Autowired
    public FileCourseRepository(@Value("${app.data.dir:data}") String dataDir) {
        this.filePath = Paths.get(dataDir, "courses.csv");
    }

    public FileCourseRepository(Path customFilePath) {
        this.filePath = customFilePath;
    }

    @PostConstruct
    public synchronized void init() {
        try {
            if (!Files.exists(filePath)) {
                seedInitialData();
            } else {
                loadFromFile();
                if (courseList.isEmpty()) {
                    seedInitialData();
                }
            }
        } catch (IOException e) {
            System.err.println("[FileCourseRepository] Warning: Could not initialize course CSV: " + e.getMessage());
        }
    }

    private synchronized void loadFromFile() throws IOException {
        courseList.clear();
        courseMap.clear();

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
                    String code = tokens.get(1);
                    String name = tokens.get(2);
                    int credits = Integer.parseInt(tokens.get(3));
                    int capacity = Integer.parseInt(tokens.get(4));

                    Course course = new Course(id, code, name, credits, capacity);
                    courseList.add(course);
                    courseMap.put(code.toUpperCase(), course);
                    if (id > maxId) maxId = id;
                } catch (Exception ex) {
                    System.err.println("[FileCourseRepository] Skipped corrupted course row: " + line);
                }
            }
        }
        idSequence.set(Math.max(maxId + 1, 100));
    }

    private synchronized void flushToFile() {
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        for (Course c : courseList) {
            List<String> tokens = Arrays.asList(
                    String.valueOf(c.getId()),
                    c.getCourseCode(),
                    c.getCourseName(),
                    String.valueOf(c.getCredits()),
                    String.valueOf(c.getMaxCapacity())
            );
            lines.add(CsvHelper.formatLine(tokens));
        }

        try {
            CsvHelper.writeLinesAtomically(filePath, lines);
        } catch (IOException e) {
            System.err.println("[FileCourseRepository] Failed to write course CSV: " + e.getMessage());
        }
    }

    private synchronized void seedInitialData() throws IOException {
        courseList.clear();
        courseMap.clear();

        List<Course> initialSeeds = List.of(
                new Course(1L, "CS201", "Data Structures & Algorithms", 4, 40),
                new Course(2L, "CS302", "Object Oriented Programming (Java)", 4, 50),
                new Course(3L, "IT301", "Database Management Systems", 3, 45),
                new Course(4L, "EC204", "Digital Logic Design", 3, 35)
        );

        for (Course c : initialSeeds) {
            courseList.add(c);
            courseMap.put(c.getCourseCode().toUpperCase(), c);
        }
        idSequence.set(5L);
        flushToFile();
    }

    @Override
    public List<Course> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(courseList));
    }

    @Override
    public Optional<Course> findById(String courseCode) {
        return findByCourseCode(courseCode);
    }

    @Override
    public Optional<Course> findByCourseCode(String courseCode) {
        if (courseCode == null) return Optional.empty();
        return Optional.ofNullable(courseMap.get(courseCode.trim().toUpperCase()));
    }

    @Override
    public boolean existsById(String courseCode) {
        return existsByCourseCode(courseCode);
    }

    @Override
    public boolean existsByCourseCode(String courseCode) {
        if (courseCode == null) return false;
        return courseMap.containsKey(courseCode.trim().toUpperCase());
    }

    @Override
    public synchronized Course save(Course course) {
        if (course.getId() == null) {
            course.setId(idSequence.getAndIncrement());
        }

        String key = course.getCourseCode().toUpperCase();
        courseList.removeIf(c -> c.getCourseCode().equalsIgnoreCase(course.getCourseCode()));
        courseList.add(course);
        courseMap.put(key, course);

        flushToFile();
        return course;
    }

    @Override
    public synchronized void deleteById(String courseCode) {
        if (courseCode == null) return;
        String key = courseCode.trim().toUpperCase();
        courseMap.remove(key);
        courseList.removeIf(c -> c.getCourseCode().equalsIgnoreCase(courseCode));
        flushToFile();
    }

    @Override
    public long count() {
        return courseList.size();
    }
}
