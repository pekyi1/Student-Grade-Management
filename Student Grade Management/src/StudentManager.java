import java.util.Scanner;

public class StudentManager {
    private Student[] students;
    private int studentCount;

    public StudentManager() {
        this.students = new Student[50];
        this.studentCount = 0;
    }

    public void addStudent(Student student) { //Adds a new student to the system.
        if (studentCount < students.length) {
            students[studentCount++] = student;
            System.out.println("\n-> Student added successfully!");
            System.out.println("  Student ID: " + student.getStudentId());
            System.out.println("  Name: " + student.getName());
            System.out.println("  Type: " + student.getStudentType());
            System.out.println("  Age: " + student.getAge());
            System.out.println("  Email: " + student.getEmail());
            System.out.printf("  Passing Grade: %.0f%%%n", student.getPassingGrade());
            System.out.println("  Status: " + student.getStatus());
        } else {
            System.out.println("Cannot add student. Maximum capacity reached.");
        }
    }

    public Student findStudent(String studentId) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equals(studentId)) {
                return students[i];
            }
        }
        return null;
    }

    public void viewAllStudents(GradeManager gm) { //The GradeManager used to calculate student averages.
        if (studentCount == 0) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("\nSTUDENT LISTING");
        System.out.println("__________________________________________________________________________________");
        System.out.printf("%-8s | %-15s | %-10s | %-9s | %-10s%n", "STU ID", "NAME", "TYPE", "AVG GRADE", "STATUS");
        System.out.println("__________________________________________________________________________________");

        for (int i = 0; i < studentCount; i++) {
            double avg = gm.calculateOverallAverage(students[i].getStudentId());
            int enrolledSubjects = gm.getEnrolledSubjectCount(students[i].getStudentId());
            students[i].displayStudentDetails(avg, enrolledSubjects);
            System.out.println("__________________________________________________________________________________");
        }
        System.out.println("\nTotal Students: " + studentCount);
        System.out.printf("Average Class Grade: %.1f%%%n", getAverageClassGrade(gm));

        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine(); //this pauses the program in you press enter
    }

    public double getAverageClassGrade(GradeManager gm) {
        if (studentCount == 0)
            return 0.0;

        double totalAverage = 0.0;
        for (int i = 0; i < studentCount; i++) {
            totalAverage += gm.calculateOverallAverage(students[i].getStudentId());
        }
        return totalAverage / studentCount;
    }

    public int getStudentCount() {
        return studentCount;
    }
}
