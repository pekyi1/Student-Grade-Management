package services;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import interfaces.Exportable;
import models.Student;
import models.Grade;
import utils.DataSerializer;

/**
 * Handles file export operations using NIO.2.
 * Supports CSV, JSON, and Binary formats.
 */
public class FileExporter {
    private static final String DATA_DIR = "data";
    private static final String CSV_DIR = DATA_DIR + "/csv";
    private static final String JSON_DIR = DATA_DIR + "/json";
    private static final String BINARY_DIR = DATA_DIR + "/binary";

    public FileExporter() {
        createDirectories();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(CSV_DIR));
            Files.createDirectories(Paths.get(JSON_DIR));
            Files.createDirectories(Paths.get(BINARY_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create data directories: " + e.getMessage());
        }
    }

    // Compatibility for legacy calls (defaults to CSV directory but generic name)
    public String exportToFile(String filename, String content) throws IOException {
        Path path = Paths.get(CSV_DIR, ensureExtension(filename, ".txt"));
        // However, if the filename implies CSV, we might want .csv
        if (filename.endsWith(".csv")) {
            path = Paths.get(CSV_DIR, filename);
        }
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        return path.toAbsolutePath().toString();
    }

    // Compatibility alias
    public String exportList(List<? extends Exportable> items, String filename) throws IOException {
        return exportToCSV(items, filename);
    }

    public String exportToCSV(Exportable item, String filename) throws IOException {
        String content = item.toExportFormat();
        Path path = Paths.get(CSV_DIR, ensureExtension(filename, ".csv"));
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        return path.toAbsolutePath().toString();
    }

    public String exportToCSV(List<? extends Exportable> items, String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Exportable item : items) {
            sb.append(item.toExportFormat()).append(System.lineSeparator());
        }
        Path path = Paths.get(CSV_DIR, ensureExtension(filename, ".csv"));
        Files.write(path, sb.toString().getBytes(StandardCharsets.UTF_8));
        return path.toAbsolutePath().toString();
    }

    public String exportToJSON(Student student, String filename) throws IOException {
        String content = DataSerializer.toJson(student);
        Path path = Paths.get(JSON_DIR, ensureExtension(filename, ".json"));
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        return path.toAbsolutePath().toString();
    }

    // Overloaded for List<Grade> (Grade Report)
    public String exportToJSON(List<Grade> grades, String filename) throws IOException {
        String content = DataSerializer.toJson(grades);
        Path path = Paths.get(JSON_DIR, ensureExtension(filename, ".json"));
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        return path.toAbsolutePath().toString();
    }

    public String exportToBinary(Object object, String filename) throws IOException {
        Path path = Paths.get(BINARY_DIR, ensureExtension(filename, ".dat"));
        try (OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
                ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(object);
        }
        return path.toAbsolutePath().toString();
    }

    private String ensureExtension(String filename, String extension) {
        return filename.toLowerCase().endsWith(extension) ? filename : filename + extension;
    }
}
