import exceptions.InvalidDataException;
import exceptions.StudentNotFoundException;
import java.util.Scanner;

public class StudentManager {
    private Student[] students;
    private int studentCount;

    public StudentManager() {
        this.students = new Student[50];
        this.studentCount = 0;
    }

    public void addStudent(Student student) throws InvalidDataException {
        if (studentCount >= students.length) {
            throw new InvalidDataException("Cannot add student. Maximum capacity reached.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new InvalidDataException("Student name cannot be empty.");
        }
        if (student.getAge() <= 0) {
            throw new InvalidDataException("Student age must be greater than 0.");
        }
        if (findStudent(student.getStudentId()) != null) {
            throw new InvalidDataException("Student with ID " + student.getStudentId() + " already exists.");
        }

        students[studentCount++] = student;
        System.out.println("\n-> Student added successfully!");
        System.out.println("  Student ID: " + student.getStudentId());
        System.out.println("  Name: " + student.getName());
        System.out.println("  Type: " + student.getStudentType());
        System.out.println("  Age: " + student.getAge());
        System.out.println("  Email: " + student.getEmail());
        System.out.printf("  Passing Grade: %.0f%%%n", student.getPassingGrade());
        System.out.println("  Status: " + student.getStatus());
    }

    public Student findStudent(String studentId) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equals(studentId)) {
                return students[i];
            }
        }
        return null; // Returning null here to allow caller to decide if it's an exception or just a
                     // check
    }

    public Student getStudent(String studentId) throws StudentNotFoundException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        return student;
    }

    public void viewAllStudents(GradeManager gm) {
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
        new Scanner(System.in).nextLine();
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
