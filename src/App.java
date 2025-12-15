
import exceptions.InvalidDataException;
import exceptions.InvalidGradeException;
import exceptions.StudentNotFoundException;
import utils.Logger;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import models.*;
import services.*;

// This class is the main entry point for the Student Grade Management System
public class App {
    private static StudentManager studentManager = new StudentManager();
    private static GradeManager gradeManager = new GradeManager();
    private static ReportGenerator reportGenerator = new ReportGenerator();
    private static FileExporter fileExporter = new FileExporter();
    private static GPACalculator gpaCalculator = new GPACalculator();
    private static BulkImportService bulkImportService = new BulkImportService();
    private static ClassStatistics classStatistics = new ClassStatistics();
    private static StudentSearchService studentSearchService = new StudentSearchService();
    private static BatchReportService batchReportService = new BatchReportService();
    private static StatisticsDashboardService statisticsDashboardService = new StatisticsDashboardService();
    private static TaskScheduler taskScheduler = new TaskScheduler();
    private static PatternSearchService patternSearchService = new PatternSearchService();
    private static AuditLogService auditLogService = new AuditLogService();
    private static StreamDataService streamDataService = new StreamDataService();
    private static DirectoryWatcherService directoryWatcherService;
    private static Scanner scanner = new Scanner(System.in);

    /**
     * The main entry point for the Student Grade Management System.
     * Initializes services and handles the main application loop.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {

        // Start Directory Watcher
        directoryWatcherService = new DirectoryWatcherService("imports", bulkImportService, studentManager,
                gradeManager);
        Thread watcherThread = new Thread(directoryWatcherService);
        watcherThread.setDaemon(true); // Ensure it dies with the app
        watcherThread.start();

        // Load persistents schedules
        taskScheduler.loadSchedules(studentManager, gradeManager);

        // US-8: Schedule background cache refresh (if not already scheduled)
        boolean cacheTaskExists = taskScheduler.getActiveTasks().stream()
                .anyMatch(t -> t.getName().equals("Cache Refresh"));
        if (!cacheTaskExists) {
            taskScheduler.scheduleTask("Cache Refresh", () -> gradeManager.refreshAllCache(studentManager), 0, 5,
                    java.util.concurrent.TimeUnit.MINUTES);
        }

        // Load persistent data
        studentManager.loadStudents();
        gradeManager.loadGrades();

        // Seed data ONLY if system is empty
        if (studentManager.getAllStudents().isEmpty()) {
            DataSeeder.seedStudents(studentManager, gradeManager);
        }

        // Inject Audit Service
        studentManager.setAuditService(auditLogService);
        gradeManager.setAuditService(auditLogService);

        boolean running = true;
        while (running) {
            try {
                printMenu();
                int choice = getIntInput("Enter choice: ");

                switch (choice) {
                    case 1:
                        addNewStudent();
                        break;
                    case 2:
                        studentManager.viewAllStudents(gradeManager);
                        break;
                    case 3:
                        recordGrade();
                        break;
                    case 4:
                        viewGradeReport();
                        break;
                    case 5:
                        exportGradeReport();
                        break;
                    case 6:
                        handleMultiFormatImport();
                        break;
                    case 7:
                        bulkImportGrades();
                        break;
                    case 8:
                        calculateStudentGPA();
                        break;
                    case 9:
                        viewClassStatistics();
                        break;
                    case 10:
                        statisticsDashboardService.startDashboard(studentManager, gradeManager, taskScheduler, scanner);
                        break;
                    case 11:
                        generateBatchReports();
                        break;
                    case 12:
                        searchStudents(); // Advanced is handled within
                        break;
                    case 13:
                        handlePatternSearch();
                        break;
                    case 14:
                        queryGradeHistory();
                        break;
                    case 15:
                        manageScheduledTasks();
                        break;
                    case 16:
                        viewSystemPerformance();
                        break;
                    case 17:
                        handleCacheManagement();
                        break;
                    case 18:
                        handleAuditTrail();
                        break;
                    case 19:
                        running = false;
                        directoryWatcherService.stop();
                        statisticsDashboardService.shutdown();
                        taskScheduler.shutdown();
                        auditLogService.shutdown();
                        System.out.println("Thank you for using the Student Grade Management System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                Logger.logError("An unexpected error occurred.", e);
                System.out.println("Recovering from error... Please try again.");
            }
        }
        scanner.close();
    }

    /**
     * Displays the main menu options to the user.
     */
    private static void printMenu() {
        System.out.println("");
        System.out.println("+================================================+");
        System.out.println("|    STUDENT GRADE MANAGEMENT - MAIN MENU        |");
        System.out.println("|         [Advanced Edition v3.0]                |");
        System.out.println("+================================================+");
        System.out.println("");
        System.out.println("STUDENT MANAGEMENT");
        System.out.println("1. Add Student (with validation)");
        System.out.println("2. View Students");
        System.out.println("3. Record Grade");
        System.out.println("4. View Grade Report");
        System.out.println("");
        System.out.println("FILE OPERATIONS");
        System.out.println("5. Export Grade Report (CSV/JSON/Binary)");
        System.out.println("6. Import Data (Multi-format support)        [ENHANCED]");
        System.out.println("7. Bulk Import Grades");
        System.out.println("");
        System.out.println("ANALYTICS & REPORTING");
        System.out.println("8. Calculate Student GPA");
        System.out.println("9. View Class Statistics");
        System.out.println("10. Real-Time Statistics Dashboard           [NEW]");
        System.out.println("11. Generate Batch Reports                   [NEW]");
        System.out.println("");
        System.out.println("SEARCH & QUERY");
        System.out.println("12. Search Students (Advanced)               [ENHANCED]");
        System.out.println("13. Pattern-Based Search                     [NEW]");
        System.out.println("14. Query Grade History                      [NEW]");
        System.out.println("");
        System.out.println("ADVANCED FEATURES");
        System.out.println("15. Schedule Automated Tasks                 [NEW]");
        System.out.println("16. View System Performance                  [NEW]");
        System.out.println("17. Cache Management                         [NEW]");
        System.out.println("18. Audit Trail Viewer                       [NEW]");
        System.out.println("");
        System.out.println("19. Exit");
        System.out.println("");

        long activeTasks = taskScheduler.getActiveTasks().size();
        System.out.println("Background Tasks: [ACTIVE] " + activeTasks + " | [STATS] Updating...");
        System.out.println("");
    }

    /**
     * Handles the logic for adding a new student to the system.
     * Prompts for user input and validates data before creating a student.
     */
    private static void addNewStudent() {
        System.out.println("\nADD STUDENT (with validation)");
        System.out.println("__________________________________________________________________________________");
        try {
            String name;
            while (true) {
                name = getStringInput("Enter Student Name: ");
                try {
                    utils.ValidationUtils.validateName(name);
                    break;
                } catch (InvalidDataException e) {
                    System.out.println("X VALIDATION ERROR: " + e.getMessage());
                    System.out.println("  Pattern required: Only letters, spaces, hyphens, and apostrophes");
                }
            }

            int age;
            while (true) {
                age = getIntInput("Enter Student Age: ");
                try {
                    utils.ValidationUtils.validateAge(age);
                    break;
                } catch (InvalidDataException e) {
                    System.out.println("X VALIDATION ERROR: " + e.getMessage());
                }
            }

            String email;
            while (true) {
                email = getStringInput("Enter Email Address: ");
                try {
                    utils.ValidationUtils.validateEmail(email);
                    break;
                } catch (InvalidDataException e) {
                    System.out.println("X VALIDATION ERROR: " + e.getMessage());
                    System.out.println("  Pattern required: username@domain.extension");
                }
            }

            String phone;
            while (true) {
                phone = getStringInput("Enter Phone Number: ");
                try {
                    utils.ValidationUtils.validatePhone(phone);
                    break;
                } catch (InvalidDataException e) {
                    System.out.println("X VALIDATION ERROR: " + e.getMessage());
                    System.out
                            .println("  Accepted patterns: (123) 456-7890, 123-456-7890, +1-123-456-7890, 1234567890");
                }
            }

            String enrollmentDate;
            while (true) {
                enrollmentDate = getStringInput("Enter Enrollment Date (YYYY-MM-DD): ");
                try {
                    utils.ValidationUtils.validateDate(enrollmentDate);
                    break;
                } catch (InvalidDataException e) {
                    System.out.println("X VALIDATION ERROR: " + e.getMessage());
                    System.out.println("  Pattern required: YYYY-MM-DD (e.g. 2024-11-03)");
                }
            }

            Student student = null;
            boolean selectingType = true;
            while (selectingType) {
                System.out.println("\nStudent Type:");
                System.out.println("1. Regular Student");
                System.out.println("2. Honors Student");
                int typeChoice = getIntInput("\nSelect type (1-2): ");

                try {
                    if (typeChoice == 1) {
                        student = new RegularStudent(name, age, email, phone, enrollmentDate);
                        selectingType = false;
                    } else if (typeChoice == 2) {
                        student = new HonorsStudent(name, age, email, phone, enrollmentDate);
                        selectingType = false;
                    } else {
                        System.out.println("Invalid student type.");
                        String retry = getStringInput("Try again? (Y/N): ");
                        if (!retry.equalsIgnoreCase("y")) {
                            System.out.println("Student not added.");
                            return;
                        }
                    }
                } catch (InvalidDataException e) {
                    System.out.println("Error creating student: " + e.getMessage());
                    return;
                }
            }

            studentManager.addStudent(student);
            System.out.println("\n✓ Student added successfully!");
            System.out.println("  All inputs validated with regex patterns");
            System.out.println("  Student ID: " + student.getStudentId());
            System.out.println("  Name: " + student.getName());
            System.out.println("  Email: " + student.getEmail());
            System.out.println("  Enrolled: " + student.getEnrollmentDate());

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        } catch (Exception e) { // Catch generic to be safe
            Logger.logError("Failed to add student", e);
            System.out.println("X ERROR: " + e.getMessage());
        }
    }

    /**
     * Allows users to record a grade for a specific student and subject.
     * Validates student ID and subject before saving.
     */
    private static void recordGrade() {
        System.out.println("\nRECORD GRADE");
        System.out.println("__________________________________________________________________________________");
        boolean recording = true;
        while (recording) {
            try {
                String studentId = getValidInput("Enter Student ID: ", (id) -> {
                    utils.ValidationUtils.validateStudentId(id);
                    studentManager.getStudent(id); // Verify existence
                });
                if (studentId == null)
                    return;

                Student student = studentManager.getStudent(studentId);

                System.out.println("\nStudent Details:");
                System.out.println("Name: " + student.getName());
                System.out.println("Type: " + student.getStudentType() + " Student");
                System.out.printf("Current Average: %.1f%%%n", gradeManager.calculateOverallAverage(studentId));

                Subject subject = null;
                String subjectName = "";
                String subjectCode = "";
                boolean selectingSubject = true;

                while (selectingSubject) {
                    System.out.println("\nSubject type:");
                    System.out.println("1. Core Subject");
                    System.out.println("2. Elective Subject");
                    int subjectTypeChoice = getIntInput("\nSelect type (1-2): ");

                    String selectedType = "";
                    if (subjectTypeChoice == 1) {
                        selectedType = "Core";
                    } else if (subjectTypeChoice == 2) {
                        selectedType = "Elective";
                    } else {
                        System.out.println("Invalid subject type.");
                        String retry = getStringInput("Try again? (Y/N): ");
                        if (!retry.equalsIgnoreCase("y")) {
                            System.out.println("Grade not recorded.");
                            return;
                        }
                        continue;
                    }

                    // Dynamic Listing
                    List<Subject> uniqueSubjects = gradeManager.getAllUniqueSubjects();
                    List<Subject> filteredSubjects = new ArrayList<>();
                    for (Subject s : uniqueSubjects) {
                        if (s.getSubjectType().equalsIgnoreCase(selectedType)) {
                            filteredSubjects.add(s);
                        }
                    }
                    // Sort by name
                    Collections.sort(filteredSubjects, new Comparator<Subject>() {
                        @Override
                        public int compare(Subject s1, Subject s2) {
                            return s1.getSubjectName().compareToIgnoreCase(s2.getSubjectName());
                        }
                    });

                    System.out.println("\nAvailable " + selectedType + " Subjects:");
                    int i = 0;
                    for (Subject s : filteredSubjects) {
                        System.out.println((i + 1) + ". " + s.getSubjectName());
                        i++;
                    }
                    int otherIndex = i + 1;
                    System.out.println(otherIndex + ". Other (Add New)");

                    int subjectChoice = getIntInput("\nSelect subject (1-" + otherIndex + "): ");

                    if (subjectChoice > 0 && subjectChoice <= filteredSubjects.size()) {
                        subject = filteredSubjects.get(subjectChoice - 1);
                        subjectName = subject.getSubjectName();
                        subjectCode = subject.getSubjectCode();
                        selectingSubject = false;
                    } else if (subjectChoice == otherIndex) {
                        System.out.println("\nEnter New Subject Details:");
                        subjectName = getStringInput("Subject Name: ");
                        subjectCode = getStringInput("Subject Code: ");
                        if (subjectName.trim().isEmpty() || subjectCode.trim().isEmpty()) {
                            System.out.println("Name and Code cannot be empty.");
                            continue;
                        }

                        if (selectedType.equals("Core")) {
                            subject = new CoreSubject(subjectName, subjectCode);
                        } else {
                            subject = new ElectiveSubject(subjectName, subjectCode);
                        }
                        selectingSubject = false;
                    } else {
                        System.out.println("Invalid subject choice.");
                        String retry = getStringInput("Try again? (Y/N): ");
                        if (!retry.equalsIgnoreCase("y")) {
                            // Loop continues
                        }
                    }
                }

                boolean enteringGrade = true;
                while (enteringGrade) {
                    double gradeValue = 0;
                    try {
                        gradeValue = getDoubleInput("\nEnter grade (0-100): ");

                        Grade grade = new Grade(studentId, subject, gradeValue);

                        System.out.println("\nGRADE CONFIRMATION");
                        System.out
                                .println(
                                        "__________________________________________________________________________________");
                        System.out.println("Grade ID: " + grade.getGradeID());
                        System.out.println("Student: " + studentId + " - " + student.getName());
                        System.out.println("Subject: " + subjectName + " (" + subject.getSubjectType() + ")");
                        System.out.printf("Grade: %.1f%%%n", gradeValue);
                        System.out.println("Date: " + grade.getDate());
                        System.out
                                .println(
                                        "__________________________________________________________________________________");

                        String confirm = getStringInput("\nConfirm grade? (Y/N): ");

                        if (confirm.equalsIgnoreCase("y")) {
                            gradeManager.addGrade(grade);
                            System.out.println("\nPress Enter to continue...");
                            scanner.nextLine();
                            System.out.println("✓ Grade recorded successfully!");
                        } else {
                            System.out.println("Grade recording cancelled.");
                        }
                        enteringGrade = false;
                        recording = false; // Exit loop after successful recording or cancellation
                    } catch (IllegalArgumentException e) {
                        System.out.println("\nX ERROR: You entered an invalid grade value");
                        System.out.println(e.getMessage());
                        System.out.println("You entered: " + gradeValue);

                        String retry = getStringInput("\nTry again? (Y/N): ");
                        if (!retry.equalsIgnoreCase("y")) {
                            enteringGrade = false;
                            recording = false;
                        }
                    }
                }
            } catch (StudentNotFoundException e) {
                Logger.logError("Student not found", e);
                System.out.println("X ERROR: " + e.getMessage());
                System.out.println("Available student IDs: ");
                for (Student s : studentManager.getAllStudents()) {
                    System.out.println("- ID: " + s.getStudentId() + ", Name: " + s.getName());
                }
                String retry = getStringInput("\nTry again? (Y/N): ");
                if (!retry.equalsIgnoreCase("y")) {
                    recording = false;
                }
            } catch (InvalidGradeException e) {
                Logger.logError("Invalid grade", e);
                System.out.println("X ERROR: " + e.getMessage());
                recording = false;
            } catch (InvalidDataException e) {
                Logger.logError("Invalid data", e);
                System.out.println("X ERROR: " + e.getMessage());
                recording = false;
            } catch (IllegalArgumentException e) {
                Logger.logError("Illegal argument", e);
                System.out.println("X ERROR: " + e.getMessage());
                recording = false;
            }
        }
    }

    /**
     * Displays a detailed grade report for a specific student.
     */
    private static void viewGradeReport() {
        System.out.println("\nVIEW GRADE REPORT");
        System.out.println("__________________________________________________________________________________");
        try {
            String studentId = getStringInput("\nEnter Student ID: ");
            utils.ValidationUtils.validateStudentId(studentId);
            Student student = studentManager.getStudent(studentId);
            gradeManager.viewGradesByStudent(student);
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        } catch (StudentNotFoundException e) {
            Logger.logError("Student not found", e);
            System.out.println("X ERROR: " + e.getMessage());
        } catch (InvalidDataException e) {
            Logger.logError("Invalid data input", e);
            System.out.println("X ERROR: " + e.getMessage());
        }
    }

    /**
     * Exports a student's grade report to a text file.
     * Prompts for report type (Summary/Detailed) and filename.
     */
    private static void exportGradeReport() {
        System.out.println("\nEXPORT GRADE REPORT (Multi-Format)");
        System.out.println("__________________________________________________________________________________");
        try {
            String studentId = getStringInput("Enter Student ID: ");
            utils.ValidationUtils.validateStudentId(studentId);
            Student student = studentManager.getStudent(studentId);

            System.out.println("\nStudent: " + studentId + " - " + student.getName());

            // Format Selection
            System.out.println("\nExport Format:");
            System.out.println("1. CSV (Comma-Separated Values)");
            System.out.println("2. JSON (JavaScript Object Notation)");
            System.out.println("3. Binary (Serialized Java Object)");
            System.out.println("4. All formats");
            int formatChoice = getIntInput("Select format (1-4): ");

            // Content Generation (Summary/Detailed logic)
            // Reusing existing logic for content generation string if relevant for
            // CSV/Text,
            // but for JSON/Binary we export raw objects generally.
            // The instructions imply exporting the "report".
            // Existing exportToFile took a String content.
            // New FileExporter methods take objects or strings.

            // If CSV/Text report:
            String textContent = "";
            if (formatChoice == 1 || formatChoice == 4) {
                // Ask for report type only if CSV/Text is involved to keep valid logic
                textContent = generateReportContent(student);
            }

            try {
                if (formatChoice == 1 || formatChoice == 4) {
                    String filename = "report_" + studentId;
                    String path = fileExporter.exportToFile(filename + ".txt", textContent); // Legacy txt/csv-like
                                                                                             // report
                    System.out.println("✓ Text/CSV Report exported: " + path);
                }

                if (formatChoice == 2 || formatChoice == 4) {
                    // Export Grades List as JSON
                    String filename = "grades_" + studentId;
                    String path = fileExporter.exportToJSON(gradeManager.getGradesForStudent(studentId), filename);
                    System.out.println("✓ JSON Data exported: " + path);
                }

                if (formatChoice == 3 || formatChoice == 4) {
                    // Export Student object + grades as Binary? Or just Student?
                    // Let's export the List<Grade> for now as it contains Student ID
                    String filename = "data_" + studentId;
                    java.util.List<Grade> grades = gradeManager.getGradesForStudent(studentId);
                    String path = fileExporter.exportToBinary(grades, filename); // Serializing List
                    System.out.println("✓ Binary Data exported: " + path);
                }

            } catch (java.io.IOException e) {
                Logger.logError("Export failed", e);
                System.out.println("X ERROR: Failed to export. " + e.getMessage());
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();

        } catch (StudentNotFoundException e) {
            Logger.logError("Student not found", e);
            System.out.println("X ERROR: " + e.getMessage());
        } catch (InvalidDataException e) {
            Logger.logError("Invalid data input", e);
            System.out.println("X ERROR: " + e.getMessage());
        }
    }

    private static String generateReportContent(Student student) {
        boolean selectingOption = true;
        String content = "";
        while (selectingOption) {
            System.out.println("\nReport Type:");
            System.out.println("1. Summary Report");
            System.out.println("2. Detailed Report");
            System.out.println("3. Both");
            int option = getIntInput("\nSelect option (1-3): ");

            if (option == 1) {
                content = reportGenerator.generateSummaryReport(student, gradeManager);
                selectingOption = false;
            } else if (option == 2) {
                content = reportGenerator.generateDetailedReport(student, gradeManager);
                selectingOption = false;
            } else if (option == 3) {
                content = reportGenerator.generateBothReport(student, gradeManager);
                selectingOption = false;
            } else {
                System.out.println("Invalid option.");
            }
        }
        return content;
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    @FunctionalInterface
    interface InputValidator {
        void validate(String input) throws Exception;
    }

    private static String getValidInput(String prompt, InputValidator validator) {
        while (true) {
            String input = getStringInput(prompt);
            if (input == null || input.trim().isEmpty())
                continue;
            if (input.equalsIgnoreCase("EXIT"))
                return null;

            try {
                validator.validate(input);
                return input;
            } catch (Exception e) {
                System.out.println("X ERROR: " + e.getMessage());
                System.out.println("Type 'EXIT' to cancel.");
            }
        }
    }

    /**
     * Calculates and displays a student's GPA and class rank.
     * Uses a 4.0 scale for GPA calculation.
     */
    private static void calculateStudentGPA() {
        System.out.println("\nCALCULATE STUDENT GPA");
        System.out.println("__________________________________________________________________________________");
        try {
            String studentId = getStringInput("Enter Student ID: ");
            utils.ValidationUtils.validateStudentId(studentId);
            Student student = studentManager.getStudent(studentId);

            System.out.println("\nStudent: " + studentId + " " + student.getName());
            System.out.println("Type: " + student.getStudentType() + " Student");
            double overallAverage = gradeManager.calculateOverallAverage(studentId);
            System.out.printf("Overall Average: %.1f%%%n", overallAverage);

            java.util.List<Grade> grades = gradeManager.getGradesForStudent(studentId);
            int rank = gradeManager.calculateClassRank(studentId);
            int totalStudents = gradeManager.getTotalStudentsWithGrades();

            String report = gpaCalculator.generateGPAReport(student, grades, rank, totalStudents, overallAverage);
            System.out.println(report);

            System.out.println("Press Enter to continue...");
            scanner.nextLine();

        } catch (StudentNotFoundException e) {
            Logger.logError("Student not found", e);
            System.out.println("X ERROR: " + e.getMessage());
        } catch (InvalidDataException e) {
            Logger.logError("Invalid data input", e);
            System.out.println("X ERROR: " + e.getMessage());
        }
    }

    /**
     * Imports grades in bulk from a CSV file.
     * Requires a CSV file in the 'imports/' directory with specific format.
     */
    private static void bulkImportGrades() {
        System.out.println("\nBULK IMPORT GRADES");
        System.out.println("__________________________________________________________________________________");
        System.out.println("Place your CSV file in: imports/");
        System.out.println("CSV Format Required:");
        System.out.println("StudentID, SubjectName, Subject Type, Grade");
        System.out.println("Example: STU001, Mathematics, Core, 85");

        String filename = getStringInput("\nEnter filename (without extension): ");
        bulkImportService.importGrades(filename, studentManager, gradeManager);

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Views statistics for the entire class, including averages and grade
     * distribution.
     */
    private static void viewClassStatistics() {
        System.out.println("\nVIEW CLASS STATISTICS");
        System.out.println("__________________________________________________________________________________");

        java.util.List<Grade> allGrades = gradeManager.getAllGrades();
        java.util.List<Student> allStudents = studentManager.getAllStudents();

        String report = classStatistics.generateClassStatisticsReport(allGrades, allStudents);
        System.out.println(report);

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Allows searching for students by various criteria (ID, Name, Grade Range,
     * Type).
     */
    private static void searchStudents() {
        boolean searching = true;
        while (searching) {
            System.out.println("\nSEARCH STUDENTS");
            System.out.println("__________________________________________________");
            System.out.println("Search options:");
            System.out.println("1. By Student ID");
            System.out.println("2. By Name (partial match)");
            System.out.println("3. By Grade Range");
            System.out.println("4. By Student Type");
            System.out.println("5. Back to Main Menu");
            System.out.print("Select option (1-5): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    studentSearchService.searchById(scanner, studentManager, gradeManager);
                    break;
                case "2":
                    studentSearchService.searchByName(scanner, studentManager, gradeManager);
                    break;
                case "3":
                    studentSearchService.searchByGradeRange(scanner, studentManager, gradeManager);
                    break;
                case "4":
                    studentSearchService.searchByType(scanner, studentManager, gradeManager);
                    break;
                case "5":
                    searching = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Generates reports for all students using concurrent processing.
     * Includes advanced configuration for scope, format, and threading.
     */
    private static void generateBatchReports() {
        System.out.println("\nGENERATE BATCH REPORTS");
        System.out.println("__________________________________________________________________________________");

        // 1. Report Scope
        System.out.println("\nReport Scope:");
        System.out.println("1. All Students (" + studentManager.getStudentCount() + " students)");
        System.out.println("2. By Student Type (Regular/Honors)");
        System.out.println("3. By Grade Range");
        System.out.println("4. Custom Selection");
        int scopeChoice = getIntInput("Select scope (1-4): ");

        BatchReportService.ReportScope scope = BatchReportService.ReportScope.ALL;
        String filterValue = "";

        if (scopeChoice == 2) {
            scope = BatchReportService.ReportScope.TYPE;
            System.out.println("Enter Type (Regular/Honors): ");
            filterValue = scanner.nextLine().trim();
        } else if (scopeChoice == 3) {
            scope = BatchReportService.ReportScope.GRADE_RANGE;
            System.out.println("Enter Min Grade (e.g. >90): ");
            filterValue = scanner.nextLine().trim();
        } else if (scopeChoice == 4) {
            scope = BatchReportService.ReportScope.CUSTOM;
            System.out.println("Enter Student IDs (comma separated): ");
            filterValue = scanner.nextLine().trim();
        }

        // 2. Report Format
        System.out.println("\nReport Format:");
        System.out.println("1. PDF Summary");
        System.out.println("2. Detailed Text");
        System.out.println("3. Excel Spreadsheet");
        System.out.println("4. All Formats");
        int formatChoice = getIntInput("Select format (1-4): ");

        BatchReportService.ReportFormat format = BatchReportService.ReportFormat.TEXT;
        if (formatChoice == 1)
            format = BatchReportService.ReportFormat.PDF;
        else if (formatChoice == 3)
            format = BatchReportService.ReportFormat.EXCEL;
        else if (formatChoice == 4)
            format = BatchReportService.ReportFormat.ALL;

        // 3. Concurrency
        System.out.println("\nConcurrency Settings:");
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available Processors: " + availableProcessors);
        System.out.println("Recommended Threads: " + (availableProcessors / 2) + "-" + availableProcessors);

        int threads = getIntInput("Enter number of threads (1-" + (availableProcessors * 2) + "): ");

        // Execute
        BatchReportService.BatchConfig config = new BatchReportService.BatchConfig(scope, format, threads, filterValue);
        batchReportService.executeBatch(studentManager.getAllStudents(), gradeManager, config);

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void manageScheduledTasks() {
        System.out.println("\nSCHEDULED TASKS MANAGEMENT");
        System.out.println("__________________________________________________________________________________");

        System.out.println("Active Tasks:");
        java.util.List<TaskScheduler.ScheduledTaskInfo> tasks = taskScheduler.getActiveTasks();
        if (tasks.isEmpty()) {
            System.out.println("No active tasks.");
        } else {
            for (TaskScheduler.ScheduledTaskInfo info : tasks) {
                long delay = info.getDelay(java.util.concurrent.TimeUnit.SECONDS);
                System.out.printf("- %s (Every %d %s) - Next run in: %d seconds%n",
                        info.getName(), info.getPeriod(), info.getUnit(), delay);
            }
        }

        System.out.println("\nOptions:");
        System.out.println("1. Schedule Daily Backup");
        System.out.println("2. Schedule Custom Check (Demo)");
        System.out.println("3. Back to Main Menu");

        int choice = getIntInput("Select option: ");
        if (choice == 1) {
            taskScheduler.scheduleTask("Daily Backup", AutomatedTasks.createDailyBackupTask(), 0, 24,
                    java.util.concurrent.TimeUnit.HOURS);
            System.out.println("Backup Scheduled.");
        } else if (choice == 2) {
            taskScheduler.scheduleTask("System Check", () -> System.out.println("System Check OK"), 0, 10,
                    java.util.concurrent.TimeUnit.SECONDS);
            System.out.println("System Check Scheduled (Every 10s).");
        }
    }

    private static void handlePatternSearch() {
        System.out.println("\nPATTERN-BASED SEARCH");
        System.out.println("__________________________________________________");
        System.out.println("1. Email Domain Pattern (e.g., @university.edu)");
        System.out.println("2. Phone Area Code Pattern (e.g., 555)");
        System.out.println("3. Student ID Pattern (e.g., STU0**)");
        System.out.println("4. Name Pattern (regex)");
        System.out.println("5. Custom Regex Pattern");

        int type = getIntInput("Select type (1-5): ");
        String regex = "";
        java.util.function.Function<models.Student, String> extractor = null;

        scanner.nextLine(); // consume newline

        // Build regex based on type or ask user
        try {
            switch (type) {
                case 1:
                    System.out.print("Enter email domain pattern: ");
                    String domain = scanner.nextLine().trim();
                    regex = ".*" + java.util.regex.Pattern.quote(domain) + "$";
                    extractor = models.Student::getEmail;
                    break;
                case 2:
                    System.out.print("Enter area code: ");
                    String area = scanner.nextLine().trim();
                    regex = "^" + java.util.regex.Pattern.quote(area) + ".*"; // Starts with area code
                    extractor = models.Student::getPhone;
                    break;
                case 3:
                    System.out.print("Enter ID pattern (use * for wildcard): ");
                    String idPat = scanner.nextLine().trim();
                    regex = "^" + idPat.replace("*", ".*") + "$";
                    extractor = models.Student::getStudentId;
                    break;
                case 4:
                    System.out.print("Enter name pattern (regex): ");
                    regex = scanner.nextLine().trim();
                    extractor = models.Student::getName;
                    break;
                case 5:
                    System.out.print("Enter custom regex: ");
                    regex = scanner.nextLine().trim();
                    System.out.println("Select field (1=ID, 2=Name, 3=Email, 4=Phone): ");
                    int f = getIntInput("Field: ");
                    scanner.nextLine();
                    switch (f) {
                        case 1:
                            extractor = models.Student::getStudentId;
                            break;
                        case 2:
                            extractor = models.Student::getName;
                            break;
                        case 3:
                            extractor = models.Student::getEmail;
                            break;
                        case 4:
                            extractor = models.Student::getPhone;
                            break;
                        default:
                            extractor = models.Student::getName;
                    }
                    break;
                default:
                    System.out.println("Invalid type.");
                    return;
            }

            System.out.println("Searching with regex: " + regex);
            java.util.List<models.Student> all = studentManager.getAllStudents();
            PatternSearchService.SearchResponse response = patternSearchService.searchByPattern(all, regex, extractor);

            System.out.println("Processing " + response.stats.totalScanned + " students...");
            System.out.println("\nSEARCH RESULTS (" + response.stats.matchesFound + " found)");
            System.out.println("__________________________________________________");
            System.out.printf("%-10s | %-20s | %-30s%n", "ID", "NAME", "MATCHED FIELD");
            System.out.println("__________________________________________________");

            for (PatternSearchService.SearchResult r : response.results) {
                System.out.printf("%-10s | %-20s | %s%n",
                        r.getStudent().getStudentId(),
                        r.getStudent().getName(),
                        r.getHighlightedText());
            }

            System.out.println("\nPattern Match Statistics:");
            System.out.println("  Total Students Scanned: " + response.stats.totalScanned);
            System.out.println(String.format("  Matches Found: %d (%.0f%%)",
                    response.stats.matchesFound,
                    (double) response.stats.matchesFound / response.stats.totalScanned * 100));
            System.out.println("  Search Time: " + response.stats.searchTimeMs + "ms");
            System.out.println("  Regex Complexity: " + response.stats.regexComplexityHint);

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();

        } catch (Exception e) {
            System.out.println("Error in search: " + e.getMessage());
        }
    }

    private static void handleCacheManagement() {
        System.out.println("\nCACHE MANAGEMENT");
        System.out.println("__________________________________________________");
        System.out.println("1. View Cache Statistics");
        System.out.println("2. Clear Cache");
        System.out.println("3. Back to Main Menu");
        int subChoice = getIntInput("Select option (1-3): ");

        CacheService<String, Double> cache = gradeManager.getCacheService();

        switch (subChoice) {
            case 1:
                System.out.println(cache.getStats());
                break;
            case 2:
                cache.clear();
                auditLogService.log("CLEAR_CACHE", "Cache cleared manually", "ADMIN", true);
                System.out.println("✓ Cache cleared successfully.");
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid option.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void handleAuditTrail() {
        System.out.println("\nAUDIT TRAIL");
        System.out.println("__________________________________________________");
        System.out.println("1. View Recent Logs (Last 20)");
        System.out.println("2. Search Logs by Keyword");
        System.out.println("3. Back to Main Menu");
        int choice = getIntInput("Select option (1-3): ");
        scanner.nextLine();

        switch (choice) {
            case 1:
                java.util.List<String> logs = auditLogService.getRecentLogs(20);
                if (logs.isEmpty()) {
                    System.out.println("No logs found.");
                } else {
                    for (String log : logs) {
                        System.out.println(log);
                    }
                }
                break;
            case 2:
                String keyword = getStringInput("Enter keyword: ");
                java.util.List<String> results = auditLogService.searchLogs(keyword);
                System.out.println("\nFound " + results.size() + " matches:");
                for (String res : results) {
                    System.out.println(res);
                }
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid option.");
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void handleMultiFormatImport() {
        System.out.println("\nIMPORT DATA (Multi-Format)");
        System.out.println("__________________________________________________");
        System.out.println("Supported: CSV (.csv), JSON (.json), Binary (.dat/.bin)");
        System.out.println("Place files in: imports/");
        String filename = getStringInput("Enter filename (with extension): ");
        bulkImportService.importGrades(filename, studentManager, gradeManager);
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void queryGradeHistory() {
        System.out.println("\nQUERY GRADE HISTORY");
        System.out.println("__________________________________________________");
        try {
            String studentId = getStringInput("Enter Student ID: ");
            utils.ValidationUtils.validateStudentId(studentId);
            Student s = studentManager.getStudent(studentId);
            gradeManager.viewGradesByStudent(s);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void viewSystemPerformance() {
        System.out.println("\nSYSTEM PERFORMANCE");
        System.out.println("__________________________________________________");
        long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        System.out.println("Memory Usage: " + (totalMem - freeMem) + "MB / " + totalMem + "MB");
        System.out.println("Active Threads: " + Thread.activeCount());
        System.out.println("__________________________________________________");
        System.out.println("Running Stream Performance Benchmark...");
        System.out.println(streamDataService.comparePerformance(studentManager.getAllStudents()));
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
