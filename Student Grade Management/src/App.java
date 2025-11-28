import java.util.Scanner;

/**
 * Main application class for the Student Grade Management System.
 * Handles user interaction and orchestrates the system's functionality.
 */
public class App {
    private static StudentManager studentManager = new StudentManager();
    private static GradeManager gradeManager = new GradeManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
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
                    running = false;
                    System.out.println("Thank you for using the Student Grade Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
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
        System.out.println("5. Exit");
        System.out.println("__________________________________________________________________________________");
    }

    private static void addNewStudent() {
        System.out.println("\nADD STUDENT");
        System.out.println("__________________________________________________________________________________");
        String name = getStringInput("Enter student name: ");
        int age = getIntInput("Enter student age: ");
        String email = getStringInput("Enter student email: ");
        String phone = getStringInput("Enter student phone: ");

        System.out.println("\nStudent type:");
        System.out.println("1. Regular Student (Passing grade: 50%)");
        System.out.println("2. Honors Student (Passing grade: 60%, honors recognition)");
        int typeChoice = getIntInput("\nSelect type (1-2): ");

        try {
            Student student;
            if (typeChoice == 1) {
                student = new RegularStudent(name, age, email, phone);
            } else if (typeChoice == 2) {
                student = new HonorsStudent(name, age, email, phone);
            } else {
                System.out.println("Invalid student type. Student not added.");
                return;
            }

            studentManager.addStudent(student);
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void recordGrade() {
        System.out.println("\nRECORD GRADE");
        System.out.println("__________________________________________________________________________________");
        String studentId = getStringInput("Enter Student ID: ");
        Student student = studentManager.findStudent(studentId);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("\nStudent Details:");
        System.out.println("Name: " + student.getName());
        System.out.println("Type: " + student.getStudentType() + " Student");
        System.out.printf("Current Average: %.1f%%%n", gradeManager.calculateOverallAverage(studentId));

        System.out.println("\nSubject type:");
        System.out.println("1. Core Subject (Mathematics, English, Science)");
        System.out.println("2. Elective Subject (Music, Art, Physical Education)");
        int subjectTypeChoice = getIntInput("\nSelect type (1-2): ");

        Subject subject;
        String subjectName = "";
        String subjectCode = "";

        if (subjectTypeChoice == 1) {
            System.out.println("\nAvailable Core Subjects:");
            System.out.println("1. Mathematics");
            System.out.println("2. English");
            System.out.println("3. Science");
            int subjectChoice = getIntInput("\nSelect subject (1-3): ");
            switch (subjectChoice) {
                case 1:
                    subjectName = "Mathematics";
                    subjectCode = "MAT101";
                    break;
                case 2:
                    subjectName = "English";
                    subjectCode = "ENG101";
                    break;
                case 3:
                    subjectName = "Science";
                    subjectCode = "SCI101";
                    break;
                default:
                    System.out.println("Invalid subject choice.");
                    return;
            }
            subject = new CoreSubject(subjectName, subjectCode);
        } else if (subjectTypeChoice == 2) {
            System.out.println("\nAvailable Elective Subjects:");
            System.out.println("1. Music");
            System.out.println("2. Art");
            System.out.println("3. Physical Education");
            int subjectChoice = getIntInput("\nSelect subject (1-3): ");
            switch (subjectChoice) {
                case 1:
                    subjectName = "Music";
                    subjectCode = "MUS101";
                    break;
                case 2:
                    subjectName = "Art";
                    subjectCode = "ART101";
                    break;
                case 3:
                    subjectName = "Physical Education";
                    subjectCode = "PE101";
                    break;
                default:
                    System.out.println("Invalid subject choice.");
                    return;
            }
            subject = new ElectiveSubject(subjectName, subjectCode);
        } else {
            System.out.println("Invalid subject type. Grade not recorded.");
            return;
        }

        double gradeValue = getDoubleInput("\nEnter grade (0-100): ");

        try {
            Grade grade = new Grade(studentId, subject, gradeValue);

            System.out.println("\nGRADE CONFIRMATION");
            System.out.println("__________________________________________________________________________________");
            System.out.println("Grade ID: " + grade.getGradeID());
            System.out.println("Student: " + studentId + " - " + student.getName());
            System.out.println("Subject: " + subjectName + " (" + subject.getSubjectType() + ")");
            System.out.printf("Grade: %.1f%%%n", gradeValue);
            System.out.println("Date: " + grade.getDate());
            System.out.println("__________________________________________________________________________________");

            String confirm = getStringInput("\nConfirm grade? (Y/N): ");

            if (confirm.equalsIgnoreCase("y")) {
                gradeManager.addGrade(grade);
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            } else {
                System.out.println("Grade recording cancelled.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewGradeReport() {
        System.out.println("\nVIEW GRADE REPORT");
        System.out.println("__________________________________________________________________________________");
        String studentId = getStringInput("\nEnter Student ID: ");
        Student student = studentManager.findStudent(studentId);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        gradeManager.viewGradesByStudent(student);
    }

    private static String getStringInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
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
