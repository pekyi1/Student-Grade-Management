
import exceptions.GradeManagementException;
import exceptions.InvalidDataException;
import exceptions.InvalidGradeException;
import exceptions.StudentNotFoundException;
import utils.Logger;
import java.util.Scanner;

public class App {
    private static StudentManager studentManager = new StudentManager();
    private static GradeManager gradeManager = new GradeManager();
    private static ReportGenerator reportGenerator = new ReportGenerator();
    private static FileExporter fileExporter = new FileExporter();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
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

    private static void printMenu() {
        System.out.println("\nSTUDENT GRADE MANAGEMENT SYSTEM");
        System.out.println("__________________________________________________________________________________");
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Record Grade");
        System.out.println("4. View Grade Report");
        System.out.println("5. Export Grade Report");
        System.out.println("6. Exit");
        System.out.println("__________________________________________________________________________________");
    }

    private static void addNewStudent() {
        System.out.println("\nADD STUDENT");
        System.out.println("__________________________________________________________________________________");
        try {
            String name = getStringInput("Enter student name: ");
            int age = getIntInput("Enter student age: ");
            String email = getStringInput("Enter student email: ");
            String phone = getStringInput("Enter student phone: ");

            Student student = null;
            boolean selectingType = true;
            while (selectingType) {
                System.out.println("\nStudent type:");
                System.out.println("1. Regular Student (Passing grade: 50%)");
                System.out.println("2. Honors Student (Passing grade: 60%, honors recognition)");
                int typeChoice = getIntInput("\nSelect type (1-2): ");

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
            }

            studentManager.addStudent(student);
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        } catch (InvalidDataException e) {
            Logger.logError("Failed to add student", e);
            System.out.println("X ERROR: " + e.getMessage());
        }
    }

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
                    System.out.println("1. Core Subject (Mathematics, English, Science)");
                    System.out.println("2. Elective Subject (Music, Art, Physical Education)");
                    int subjectTypeChoice = getIntInput("\nSelect type (1-2): ");

                    if (subjectTypeChoice == 1) {
                        boolean selectingCore = true;
                        while (selectingCore) {
                            System.out.println("\nAvailable Core Subjects:");
                            System.out.println("1. Mathematics");
                            System.out.println("2. English");
                            System.out.println("3. Science");
                            int subjectChoice = getIntInput("\nSelect subject (1-3): ");
                            switch (subjectChoice) {
                                case 1:
                                    subjectName = "Mathematics";
                                    subjectCode = "MAT101";
                                    selectingCore = false;
                                    break;
                                case 2:
                                    subjectName = "English";
                                    subjectCode = "ENG101";
                                    selectingCore = false;
                                    break;
                                case 3:
                                    subjectName = "Science";
                                    subjectCode = "SCI101";
                                    selectingCore = false;
                                    break;
                                default:
                                    System.out.println("Invalid subject choice.");
                                    String retry = getStringInput("Try again? (Y/N): ");
                                    if (!retry.equalsIgnoreCase("y")) {
                                        selectingCore = false;
                                        // Go back to subject type selection or exit?
                                        // User request implies "try again" for exception/invalid option.
                                        // If they say no, we should probably break out of subject selection entirely or
                                        // go back to type.
                                        // Let's assume breaking out of specific selection loop, effectively cancelling
                                        // if they say no to retry.
                                        // But here we are inside selectingCore.
                                    }
                            }
                        }
                        if (!subjectName.isEmpty()) {
                            subject = new CoreSubject(subjectName, subjectCode);
                            selectingSubject = false;
                        } else {
                            // If we exited selectingCore without a subject, ask if they want to retry
                            // subject type or exit
                            String retry = getStringInput("Try selecting subject type again? (Y/N): ");
                            if (!retry.equalsIgnoreCase("y")) {
                                return;
                            }
                        }

                    } else if (subjectTypeChoice == 2) {
                        boolean selectingElective = true;
                        while (selectingElective) {
                            System.out.println("\nAvailable Elective Subjects:");
                            System.out.println("1. Music");
                            System.out.println("2. Art");
                            System.out.println("3. Physical Education");
                            int subjectChoice = getIntInput("\nSelect subject (1-3): ");
                            switch (subjectChoice) {
                                case 1:
                                    subjectName = "Music";
                                    subjectCode = "MUS101";
                                    selectingElective = false;
                                    break;
                                case 2:
                                    subjectName = "Art";
                                    subjectCode = "ART101";
                                    selectingElective = false;
                                    break;
                                case 3:
                                    subjectName = "Physical Education";
                                    subjectCode = "PE101";
                                    selectingElective = false;
                                    break;
                                default:
                                    System.out.println("Invalid subject choice.");
                                    String retry = getStringInput("Try again? (Y/N): ");
                                    if (!retry.equalsIgnoreCase("y")) {
                                        selectingElective = false;
                                    }
                            }
                        }
                        if (!subjectName.isEmpty()) {
                            subject = new ElectiveSubject(subjectName, subjectCode);
                            selectingSubject = false;
                        } else {
                            String retry = getStringInput("Try selecting subject type again? (Y/N): ");
                            if (!retry.equalsIgnoreCase("y")) {
                                return;
                            }
                        }
                    } else {
                        System.out.println("Invalid subject type.");
                        String retry = getStringInput("Try again? (Y/N): ");
                        if (!retry.equalsIgnoreCase("y")) {
                            System.out.println("Grade not recorded.");
                            return;
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
                        System.out.println("\nX ERROR: InvalidGradeException");
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
}
