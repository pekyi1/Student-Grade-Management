package services;

import exceptions.InvalidDataException;
// import exceptions.InvalidFileFormatException; // Removed: Unused
import interfaces.CSVParser;
import models.Grade;
import models.Student;
import models.Subject;
import utils.Logger;
import utils.DataSerializer;
// import java.io.File; // Removed: Unused
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handles the bulk import of grades/data from multiple file formats.
 * Uses NIO.2 Streams for memory-efficient CSV processing.
 */
public class BulkImportService {

    private static final String IMPORT_DIR = "imports/";
    private static final String LOG_DIR = "logs/";

    // Dependencies
    // private CSVParser csvParser; // Removed unused field

    public BulkImportService() {
        createDirectory(IMPORT_DIR);
        createDirectory(LOG_DIR);
    }

    // Constructor for backwards compatibility (parser ignored)
    public BulkImportService(CSVParser parser) {
        this();
    }

    private void createDirectory(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + path);
        }
    }

    /**
     * Orchestrates the import based on file extension.
     */
    public void importGrades(String filename, StudentManager studentManager, GradeManager gradeManager) {
        Path filePath = Paths.get(IMPORT_DIR, filename);
        if (!Files.exists(filePath)) {
            // Try appending extension if missing
            if (!filename.contains(".")) {
                Path csvPath = Paths.get(IMPORT_DIR, filename + ".csv");
                if (Files.exists(csvPath)) {
                    filePath = csvPath;
                }
            } else {
                System.out.println("X ERROR: File not found: " + filePath.toAbsolutePath());
                return;
            }
        }

        if (!Files.exists(filePath)) {
            System.out.println("X ERROR: File not found: " + filePath.toAbsolutePath());
            return;
        }

        System.out.println("Processing file: " + filePath.getFileName());
        String fileNameStr = filePath.getFileName().toString().toLowerCase();

        try {
            if (fileNameStr.endsWith(".csv")) {
                importCSVStreaming(filePath, studentManager, gradeManager);
            } else if (fileNameStr.endsWith(".json")) {
                importJSON(filePath, gradeManager); // Assuming JSON structure matches
            } else if (fileNameStr.endsWith(".dat") || fileNameStr.endsWith(".bin")) {
                importBinary(filePath, gradeManager);
            } else {
                System.out.println("X ERROR: Unsupported file format.");
            }
        } catch (Exception e) {
            System.out.println("X ERROR During Import: " + e.getMessage());
            Logger.logError("Import Error", e);
        }
    }

    private void importCSVStreaming(Path path, StudentManager studentManager, GradeManager gradeManager) {
        List<String> errors = new ArrayList<>();
        // Use an array to hold counters [success, fail, total] effectively final for
        // lambda
        int[] counters = new int[3];

        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                counters[2]++; // Total
                try {
                    String[] parts = line.split(","); // Basic CSV split
                    processRow(parts, studentManager, gradeManager);
                    counters[0]++; // Success
                } catch (Exception e) {
                    counters[1]++; // Fail
                    errors.add("Row " + counters[2] + ": " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.out.println("X ERROR: IO Error reading file: " + e.getMessage());
            return;
        }

        generateImportLog(errors, counters[0], counters[1], counters[2]);
        printSummary(counters[0], counters[1], counters[2]);
    }

    private void importJSON(Path path, GradeManager gradeManager) {
        // TBD: Implement if complex JSON import is required.
        // For US-2, focusing on export is prioritized in instructions?
        // Actually US-2 says "Import and export data in multiple formats".
        // Currently DataSerializer handles JSON export. Import requires parsing
        // structure.
        System.out.println("JSON Import not yet fully implemented (Requires JSON Parser).");
    }

    private void importBinary(Path path, GradeManager gradeManager) {
        try {
            Object obj = DataSerializer.deserialize(Files.readAllBytes(path));
            if (obj instanceof List) {
                List<?> list = (List<?>) obj;
                // Assuming list of grades? Or Students? Context dependent.
                System.out.println("Binary file loaded. Contains " + list.size() + " objects.");
                // Logic to merge would go here.
            } else {
                System.out.println("Binary file loaded: " + obj.getClass().getSimpleName());
            }
        } catch (Exception e) {
            System.out.println("X Binary Import Failed: " + e.getMessage());
        }
    }

    private void processRow(String[] parts, StudentManager studentManager, GradeManager gradeManager) throws Exception {
        if (parts.length < 4) {
            throw new InvalidDataException("Invalid CSV format. Expected 4 columns.");
        }

        String studentId = parts[0].trim();
        String subjectName = parts[1].trim();
        String subjectType = parts[2].trim();
        // Assuming parts[3] is grade
        double gradeValue = Double.parseDouble(parts[3].trim());

        Student student = studentManager.getStudent(studentId); // Check existence
        Subject subject = services.SubjectFactory.createSubject(subjectName, subjectType);
        Grade grade = new Grade(studentId, subject, gradeValue);
        gradeManager.addGrade(grade);
    }

    private void generateImportLog(List<String> errors, int successCount, int failCount, int totalRows) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String logFilename = LOG_DIR + "import_log_" + timestamp + ".txt";

        try (FileWriter writer = new FileWriter(logFilename)) {
            writer.write("IMPORT LOG - " + LocalDateTime.now() + "\n");
            writer.write("__________________________________________________\n\n");
            writer.write("Total Rows Processed: " + totalRows + "\n");
            writer.write("Successfully Imported: " + successCount + "\n");
            writer.write("Failed: " + failCount + "\n");
            writer.write("__________________________________________________\n\n");

            if (!errors.isEmpty()) {
                writer.write("FAILED RECORDS:\n");
                for (String error : errors) {
                    writer.write(error + "\n");
                }
            } else {
                writer.write("No errors found.\n");
            }
            System.out.println("\nLog saved to: " + logFilename);

        } catch (IOException e) {
            System.out.println("X ERROR: Failed to write log file.");
        }
    }

    private void printSummary(int successCount, int failCount, int totalRows) {
        System.out.println("\nIMPORT SUMMARY");
        System.out.println("__________________________________________________");
        System.out.println("Total Rows: " + totalRows);
        System.out.println("Successfully Imported: " + successCount);
        System.out.println("Failed: " + failCount);
        System.out.println("\nImport completed!");
    }
}
