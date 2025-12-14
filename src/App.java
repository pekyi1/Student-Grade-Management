
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

        watcherThread.start();

        // Load persistents schedules
        taskScheduler.loadSchedules(studentManager, gradeManager);

        // Seed data for testing
        DataSeeder.seedStudents(studentManager, gradeManager);

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
                        calculateStudentGPA();
                        break;
                    case 7:
                        bulkImportGrades();
                        break;
                    case 8:
                        viewClassStatistics();
                        break;
                    case 9:
                        searchStudents();
                        break;
                    case 10:
                        // Exit was 10, moving to 11
                        // Wait, user story says "New Facets". Lets keep 10 as exit?
                        // Instructions say "10 user stories".
                        // US-4 is just "Concurrent Batch Report Generation".
                        // I'll add it as option 11 and make Exit 12?
                        // Or I can add it as option 11 if I renumber?
                        // Let's add it as option 10 and push Exit to 11.
                        // Wait, previous menu had 10 items.
                        // 1. Add Student
                        // ...
                        // 9. Search Students
                        // 10. Exit
                        // So I will make 10 -> Batch Reports, 11 -> Exit.
                        generateBatchReports();
                        break;
                    case 11:
                        statisticsDashboardService.startDashboard(studentManager, gradeManager, scanner);
                        break;
                    case 12:
                        running = false;
                        directoryWatcherService.stop();
                        statisticsDashboardService.shutdown();
                        taskScheduler.shutdown();
                        System.out.println("Thank you for using the Student Grade Management System. Goodbye!");
                        break;
                    case 13:
                        manageScheduledTasks();
                        break;
                    case 14:
                        handlePatternSearch();
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
        System.out.println("\nSTUDENT GRADE MANAGEMENT SYSTEM");
        System.out.println("__________________________________________________________________________________");
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Record Grade");
        System.out.println("4. View Grade Report");
        System.out.println("5. Export Grade Report");
        System.out.println("6. Calculate Student GPA");
        System.out.println("7. Bulk Import Grades");
        System.out.println("8. View Class Statistics");
        System.out.println("9. Search Students");
        System.out.println("10. Generate Batch Reports");
        System.out.println("11. Real-Time Statistics Dashboard");
        System.out.println("12. Exit");
        System.out.println("13. Scheduled Tasks Management");
        System.out.println("14. Advanced Pattern-Based Search");
        System.out.println("__________________________________________________________________________________");
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
                String studentId = getStringInput("Enter Student ID: ");
                utils.ValidationUtils.validateStudentId(studentId);
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
        Student[] studentsArray = studentManager.getAllStudents();
        java.util.List<Student> allStudents = java.util.Arrays.asList(studentsArray);

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
     */
    private static void generateBatchReports() {
        System.out.println("\nGENERATE BATCH REPORTS");
        System.out.println("__________________________________________________________________________________");

        Student[] studentsArray = studentManager.getAllStudents();
        java.util.List<Student> students = java.util.Arrays.asList(studentsArray);

        if (students.isEmpty()) {
            System.out.println("No students found to generate reports for.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        int threads = getIntInput("Enter number of threads (2-8): ");
        batchReportService.generateBatchReports(students, gradeManager, threads);

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
                    // Escaping user input for basic usage if they type just "@gmail.com" -> we want
                    // to match end
                    // But US says "regex patterns", so we assume they might know regex or we help
                    // them.
                    // Acceptance Example: "@university.edu" -> regex ".*@university\.edu$"
                    // Let's interpret their input as the literal domain suffix
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
                    // Simple wildcard conversion
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
            java.util.List<models.Student> all = java.util.Arrays.asList(studentManager.getAllStudents());
            PatternSearchService.SearchResponse response = patternSearchService.searchByPattern(all, regex, extractor);

            System.out.println("Processing " + response.stats.totalScanned + " students...");
            System.out.println("\nSEARCH RESULTS (" + response.stats.matchesFound + " found)");
            System.out.println("__________________________________________________");
            System.out.printf("%-10s | %-20s | %-30s%n", "ID", "NAME", "MATCHED FIELD");
            System.out.println("__________________________________________________");

            for (PatternSearchService.SearchResult r : response.results) {
                // We display the highlighted text in the 3rd column or replacing the field?
                // The screenshot shows columns ID | NAME | EMAIL (highlighted)
                // Since we have a generic extractor, we might not know 'which' column
                // corresponds to highlighted text if we print standard columns.
                // But for cases 1-4 we know clearly.
                // Let's blindly print ID | Name | HighlightedValue
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
}
