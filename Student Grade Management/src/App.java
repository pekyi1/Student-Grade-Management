import java.util.Scanner;

public class App {
    private static StudentManager studentManager = new StudentManager();
    private static GradeManager gradeManager = new GradeManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Enter your choice: ");

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
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n=== Student Grade Management System ===");
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Record Grade");
        System.out.println("4. View Grade Report");
        System.out.println("5. Exit");
    }

    private static void addNewStudent() {
        System.out.println("\n--- Add New Student ---");
        String name = getStringInput("Enter Name: ");
        int age = getIntInput("Enter Age: ");
        String email = getStringInput("Enter Email: ");
        String phone = getStringInput("Enter Phone: ");

        System.out.println("Select Student Type:");
        System.out.println("1. Regular");
        System.out.println("2. Honors");
        int typeChoice = getIntInput("Enter choice (1/2): ");

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
    }

    private static void recordGrade() {
        System.out.println("\n--- Record Grade ---");
        String studentId = getStringInput("Enter Student ID: ");
        Student student = studentManager.findStudent(studentId);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("Select Subject Type:");
        System.out.println("1. Core");
        System.out.println("2. Elective");
        int subjectTypeChoice = getIntInput("Enter choice (1/2): ");

        Subject subject;
        String subjectName = getStringInput("Enter Subject Name: ");
        String subjectCode = getStringInput("Enter Subject Code: ");

        if (subjectTypeChoice == 1) {
            subject = new CoreSubject(subjectName, subjectCode);
        } else if (subjectTypeChoice == 2) {
            subject = new ElectiveSubject(subjectName, subjectCode);
        } else {
            System.out.println("Invalid subject type. Grade not recorded.");
            return;
        }

        double gradeValue = getDoubleInput("Enter Grade (0-100): ");

        try {
            Grade grade = new Grade(studentId, subject, gradeValue);
            // Confirmation
            System.out.println("Confirm Grade Entry:");
            System.out.println("Student: " + student.getName());
            System.out.println("Subject: " + subjectName);
            System.out.println("Grade: " + gradeValue);
            String confirm = getStringInput("Confirm (y/n): ");

            if (confirm.equalsIgnoreCase("y")) {
                gradeManager.addGrade(grade);
            } else {
                System.out.println("Grade recording cancelled.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewGradeReport() {
        System.out.println("\n--- View Grade Report ---");
        String studentId = getStringInput("Enter Student ID: ");
        Student student = studentManager.findStudent(studentId);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        gradeManager.viewGradesByStudent(studentId);
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
