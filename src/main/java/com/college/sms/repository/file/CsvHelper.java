package com.college.sms.repository.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ==============================================================================
 * CsvHelper (OOP Concept: Character Streams, Buffered I/O & File Safety)
 * ==============================================================================
 * Lightweight utility for robust CSV serialization and deserialization.
 * Demonstrates:
 * 1. Character Streams: BufferedReader and BufferedWriter with UTF-8 encoding.
 * 2. File Safety: Atomic writes using temporary file swap to prevent data corruption.
 * 3. Robust CSV Tokenizer: Properly handles embedded commas and double quotes.
 * ==============================================================================
 */
public final class CsvHelper {

    private CsvHelper() {
        // Prevent instantiation of utility class
    }

    /**
     * Parses a single CSV line into tokens, respecting quotes containing commas.
     *
     * @param line Raw line text from file
     * @return List of parsed cell values
     */
    public static List<String> parseLine(String line) {
        List<String> tokens = new ArrayList<>();
        if (line == null || line.trim().isEmpty()) {
            return tokens;
        }

        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;
        int length = line.length();

        for (int i = 0; i < length; i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (insideQuotes && i + 1 < length && line.charAt(i + 1) == '"') {
                    // Escaped double quote ("")
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (c == ',' && !insideQuotes) {
                tokens.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString().trim());
        return tokens;
    }

    /**
     * Formats a list of values into a valid CSV line with escaping.
     *
     * @param values List of string values
     * @return Formatted CSV line
     */
    public static String formatLine(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            String val = values.get(i);
            if (val == null) {
                val = "";
            }

            boolean needsQuotes = val.contains(",") || val.contains("\"") || val.contains("\n") || val.contains("\r");
            if (needsQuotes) {
                sb.append('"').append(val.replace("\"", "\"\"")).append('"');
            } else {
                sb.append(val);
            }

            if (i < values.size() - 1) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    /**
     * Safely and atomically writes lines to target file using a temporary swap file.
     *
     * @param targetPath Target file path
     * @param lines      List of CSV lines to write
     * @throws IOException on I/O failure
     */
    public static synchronized void writeLinesAtomically(Path targetPath, List<String> lines) throws IOException {
        Path parent = targetPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        // Temporary file in same directory ensures atomic move support on file systems
        Path tempPath = Files.createTempFile(parent != null ? parent : Paths.get("."), "sms_tmp_", ".csv");

        try (BufferedWriter writer = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
        }

        // Atomic replace or standard replace
        try {
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Reads all lines using BufferedReader and UTF-8 charset.
     *
     * @param targetPath Target file path
     * @return List of non-empty file lines
     * @throws IOException on I/O failure
     */
    public static synchronized List<String> readLines(Path targetPath) throws IOException {
        List<String> lines = new ArrayList<>();
        if (!Files.exists(targetPath)) {
            return lines;
        }

        try (BufferedReader reader = Files.newBufferedReader(targetPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }
}
