package services;

import exceptions.InvalidDataException;
import exceptions.InvalidGradeException;
import exceptions.StudentNotFoundException;
import exceptions.InvalidFileFormatException;
import interfaces.CSVParser;
import models.Grade;
import models.Student;
import models.Subject;
import utils.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// This class handles the bulk import of grades from CSV files
public class BulkImportService {

    private static final String IMPORT_DIR = "imports/";
    private static final String LOG_DIR = "logs/";
    private CSVParser csvParser;

    public BulkImportService(CSVParser csvParser) {
        this.csvParser = csvParser;
        createDirectory(IMPORT_DIR);
        createDirectory(LOG_DIR);
    }

    // Default constructor for backward compatibility or default behavior
    public BulkImportService() {
        this(new SimpleCSVParser());
    }

    // This helper method creates a directory if it does not already exist
    private void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Orchestrates the reading, parsing, and processing of the import file.
     *
     * @param filename       The name of the CSV file to import (located in imports/
     *                       directory).
     * @param studentManager The StudentManager instance to validate students.
     * @param gradeManager   The GradeManager instance to add grades.
     */
    public void importGrades(String filename, StudentManager studentManager, GradeManager gradeManager) {
        File file = new File(IMPORT_DIR + filename);
        if (!file.exists()) {
            if (!filename.endsWith(".csv")) {
                file = new File(IMPORT_DIR + filename + ".csv");
            }
            if (!file.exists()) {
                System.out.println("X ERROR: File not found: " + file.getAbsolutePath());
                return;
            }
        }

        System.out.println("Validating file... ✓");
        System.out.println("Processing grades...");

        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        int totalRows = 0;

        try {
            List<String[]> records = csvParser.parse(file.getAbsolutePath());
            for (String[] parts : records) {
                totalRows++;
                try {
                    processRow(parts, studentManager, gradeManager);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errors.add("Row " + totalRows + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("X ERROR: Error reading file: " + e.getMessage());
            Logger.logError("File read error", e);
            return;
        } catch (InvalidFileFormatException e) {
            System.out.println("X ERROR: " + e.getMessage());
            Logger.logError("Invalid file format", e);
            errors.add("Fatal Error: " + e.getMessage());
        }

        generateImportLog(errors, successCount, failCount, totalRows);
        printSummary(successCount, failCount, totalRows);
    }

    // This method parses a single CSV row and adds the grade to the system
    private void processRow(String[] parts, StudentManager studentManager, GradeManager gradeManager)
            throws Exception {
        if (parts.length != 4) {
            throw new InvalidDataException("Invalid CSV format. Expected 4 columns.");
        }

        String studentId = parts[0].trim();
        String subjectName = parts[1].trim();
        String subjectType = parts[2].trim();
        double gradeValue;

        try {
            gradeValue = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Invalid grade format: " + parts[3]);
        }

        Student student = studentManager.getStudent(studentId); // Throws StudentNotFoundException

        Subject subject = SubjectFactory.createSubject(subjectName, subjectType);

        Grade grade = new Grade(studentId, subject, gradeValue); // Throws InvalidGradeException
        gradeManager.addGrade(grade);
    }

    // This method generates a log file detailing the results of the import
    // operation
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

            System.out.println("\nSee " + logFilename + " for details");

        } catch (IOException e) {
            System.out.println("X ERROR: Failed to write log file.");
            Logger.logError("Log write error", e);
        }
    }

    // This method prints a summary of the import results to the console
    private void printSummary(int successCount, int failCount, int totalRows) {
        System.out.println("\nIMPORT SUMMARY");
        System.out.println("__________________________________________________");
        System.out.println("Total Rows: " + totalRows);
        System.out.println("Successfully Imported: " + successCount);
        System.out.println("Failed: " + failCount);

        if (failCount > 0) {
            System.out.println("\nFailed Records (Preview):");
            // Logic to show preview if needed, but log file covers it.
        }
        System.out.println("\nImport completed!");
        System.out.println(successCount + " grades added to system");
    }
}
