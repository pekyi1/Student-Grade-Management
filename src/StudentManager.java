import exceptions.InvalidDataException;
import exceptions.StudentNotFoundException;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;

// This class manages the collection of students and deals with adding or finding them
public class StudentManager implements Searchable {
    private Student[] students;
    private int studentCount;

    public StudentManager() {
        this.students = new Student[50];
        this.studentCount = 0;
    }

    /**
     * Adds a new student to the system after performing validation checks.
     *
     * @param student The student object to be added.
     * @throws InvalidDataException If the student data is invalid (e.g., empty
     *                              name, invalid age/email/phone) or capacity is
     *                              reached.
     */
    public void addStudent(Student student) throws InvalidDataException {
        if (studentCount >= students.length) {
            throw new InvalidDataException("Cannot add student. Maximum capacity reached.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new InvalidDataException("Student name cannot be empty.");
        }
        utils.ValidationUtils.validateAge(student.getAge());
        utils.ValidationUtils.validateEmail(student.getEmail());
        utils.ValidationUtils.validatePhone(student.getPhone());

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

    /**
     * Attempts to locate a student by their unique ID.
     *
     * @param studentId The unique identifier of the student.
     * @return The Student object if found, or null if not found.
     */
    public Student findStudent(String studentId) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equals(studentId)) {
                return students[i];
            }
        }
        return null; // Returning null here to allow caller to decide if it's an exception or just a
                     // check
    }

    // This method finds all students whose names match the search string
    @Override
    public List<Student> searchByName(String name) {
        List<Student> results = new ArrayList<>();
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(students[i]);
            }
        }
        return results;
    }

    /**
     * Retrieves a student by their ID, throwing an exception if not found.
     *
     * @param studentId The unique identifier of the student.
     * @return The Student object.
     * @throws StudentNotFoundException If no student matches the provided ID.
     */
    public Student getStudent(String studentId) throws StudentNotFoundException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        return student;
    }

    // This method prints a list of all students and their summary details
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

    /**
     * Calculates the average grade across all students in the class.
     *
     * @param gm The GradeManager instance used to calculate individual student
     *           averages.
     * @return The class-wide average grade percentage.
     */
    public double getAverageClassGrade(GradeManager gm) {
        if (studentCount == 0)
            return 0.0;

        double totalAverage = 0.0;
        for (int i = 0; i < studentCount; i++) {
            totalAverage += gm.calculateOverallAverage(students[i].getStudentId());
        }
        return totalAverage / studentCount;
    }

    public Student[] getAllStudents() {
        Student[] result = new Student[studentCount];
        System.arraycopy(students, 0, result, 0, studentCount);
        return result;
    }

    public int getStudentCount() {
        return studentCount;
    }
}
