import exceptions.GradeManagementException;
import exceptions.InvalidDataException;
import exceptions.InvalidGradeException;
import exceptions.StudentNotFoundException;
import utils.Logger;
import utils.Logger;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import models.*;
import services.*;
import interfaces.*;

// This class is the main entry point for the Student Grade Management System
public class App {
    private static StudentManager studentManager = new StudentManager();
    private static GradeManager gradeManager = new GradeManager();
    private static ReportGenerator reportGenerator = new ReportGenerator();
    private static FileExporter fileExporter = new FileExporter();
    private static GPACalculator gpaCalculator = new GPACalculator();
    private static BulkImportService bulkImportService = new BulkImportService(new SimpleCSVParser());
    private static ClassStatistics classStatistics = new ClassStatistics();
    private static StudentSearchService studentSearchService = new StudentSearchService();
    private static Scanner scanner = new Scanner(System.in);

    /**
     * The main entry point for the Student Grade Management System.
     * Initializes services and handles the main application loop.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
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
                        running = false;
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
        System.out.println("10. Exit");
        System.out.println("__________________________________________________________________________________");
    }

    /**
     * Handles the logic for adding a new student to the system.
     * Prompts for user input and validates data before creating a student.
     */
    private static void addNewStudent() {
        System.out.println("\nADD STUDENT");
        System.out.println("__________________________________________________________________________________");
        try {
            String name = getStringInput("Enter student name: ");

            int age;
            while (true) {
                age = getIntInput("Enter student age: ");
                try {
                    utils.ValidationUtils.validateAge(age);
                    break;
                } catch (InvalidDataException e) {
                    System.out.println("Invalid age: " + e.getMessage());
                }
            }

            String email;
            while (true) {
                email = getStringInput("Enter student email: ");
                try {
                    utils.ValidationUtils.validateEmail(email);
                    break;
                } catch (InvalidDataException e) {
                    System.out.println("Invalid email: " + e.getMessage());
                }
            }

            String phone;
            while (true) {
                phone = getStringInput("Enter student phone (10 digits): ");
                try {
                    utils.ValidationUtils.validatePhone(phone);
                    break;
                } catch (InvalidDataException e) {
                    System.out.println("Invalid phone: " + e.getMessage());
                }
            }

            Student student = null;
            boolean selectingType = true;
            while (selectingType) {
                System.out.println("\nStudent type:");
                System.out.println("1. Regular Student (Passing grade: 50%)");
                System.out.println("2. Honors Student (Passing grade: 60%, honors recognition)");
                int typeChoice = getIntInput("\nSelect type (1-2): ");

                try {
                    if (typeChoice == 1) {
                        student = new RegularStudent(name, age, email, phone);
                        selectingType = false;
                    } else if (typeChoice == 2) {
                        student = new HonorsStudent(name, age, email, phone);
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
                    return; // Should not happen if inputs are validated, but good for safety
                }
            }

            studentManager.addStudent(student);
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        } catch (InvalidDataException e) {
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
            Student student = studentManager.getStudent(studentId);
            gradeManager.viewGradesByStudent(student);
        } catch (StudentNotFoundException e) {
            Logger.logError("Student not found", e);
            System.out.println("X ERROR: " + e.getMessage());
        }
    }

    /**
     * Exports a student's grade report to a text file.
     * Prompts for report type (Summary/Detailed) and filename.
     */
    private static void exportGradeReport() {
        System.out.println("\nEXPORT GRADE REPORT");
        System.out.println("__________________________________________________________________________________");
        try {
            String studentId = getStringInput("Enter Student ID: ");
            Student student = studentManager.getStudent(studentId);

            System.out.println("\nStudent: " + studentId + " " + student.getName());
            System.out.println("Type: " + student.getStudentType() + " Student");
            System.out.println("Total Grades: " + gradeManager.getEnrolledSubjectCount(studentId));

            boolean selectingOption = true;
            String content = "";
            while (selectingOption) {
                System.out.println("\nExport options:");
                System.out.println("1. Summary Report (overview only)");
                System.out.println("2. Detailed Report (all grades)");
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
                    String retry = getStringInput("Try again? (Y/N): ");
                    if (!retry.equalsIgnoreCase("y")) {
                        System.out.println("Export cancelled.");
                        return;
                    }
                }
            }

            String filename = getStringInput("\nEnter filename (without extension): ");
            try {
                String filePath = fileExporter.exportToFile(filename, content);
                java.io.File file = new java.io.File(filePath);
                System.out.println("\n✓ Report exported successfully!");
                System.out.println("File: " + file.getName());
                System.out.println("Location: " + filePath);
                System.out.println("Size: " + file.length() + " bytes");
            } catch (java.io.IOException e) {
                Logger.logError("Export failed", e);
                System.out.println("X ERROR: Failed to export report. " + e.getMessage());
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();

        } catch (StudentNotFoundException e) {
            Logger.logError("Student not found", e);
            System.out.println("X ERROR: " + e.getMessage());
        }
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
}
