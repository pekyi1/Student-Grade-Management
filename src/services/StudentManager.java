package services;

import exceptions.InvalidDataException;
import exceptions.StudentNotFoundException;
import models.Student;
import interfaces.Searchable;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;

// This class manages the collection of students and deals with adding or finding them
public class StudentManager implements Searchable {
    // US-1: Use HashMap<String, Student> for O(1) student lookup by ID
    private java.util.Map<String, Student> students;
    // We don't need studentCount as the Map tracks size

    public StudentManager() {
        this.students = new java.util.HashMap<>();
    }

    /**
     * Adds a new student to the system after performing validation checks.
     * Time Complexity: O(1) - HashMap insertion is constant time on average.
     *
     * @param student The student object to be added.
     * @throws InvalidDataException If the student data is invalid or ID already
     *                              exists.
     */
    public void addStudent(Student student) throws InvalidDataException {
        // No capacity check needed for HashMap (dynamically resizes)
        // Check for empty name
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new InvalidDataException("Student name cannot be empty.");
        }

        utils.ValidationUtils.validateAge(student.getAge());
        utils.ValidationUtils.validateEmail(student.getEmail());
        utils.ValidationUtils.validatePhone(student.getPhone());

        // Check if ID exists - O(1)
        if (students.containsKey(student.getStudentId())) {
            throw new InvalidDataException("Student with ID " + student.getStudentId() + " already exists.");
        }

        students.put(student.getStudentId(), student);

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
     * Time Complexity: O(1) - HashMap lookup is constant time on average.
     *
     * @param studentId The unique identifier of the student.
     * @return The Student object if found, or null if not found.
     */
    public Student findStudent(String studentId) {
        return students.get(studentId);
    }

    /**
     * Finds all students whose names match the search string.
     * Time Complexity: O(n) - Must iterate through all values to check names.
     */
    @Override
    public List<Student> searchByName(String name) {
        List<Student> results = new ArrayList<>();
        String searchLower = name.toLowerCase();
        for (Student s : students.values()) {
            if (s.getName().toLowerCase().contains(searchLower)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Retrieves a student by their ID, throwing an exception if not found.
     * Time Complexity: O(1)
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
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("\nSTUDENT LISTING");
        System.out.println("__________________________________________________________________________________");
        System.out.printf("%-8s | %-15s | %-10s | %-9s | %-10s%n", "STU ID", "NAME", "TYPE", "AVG GRADE", "STATUS");
        System.out.println("__________________________________________________________________________________");

        // Use TreeMap for ID sorting if we want persistent order, or just iterate
        // values
        // US-1 mentions using correct collections. HashMap doesn't guarantee order.
        // If display order matters, we could use values() or sort them.
        // For now, simple iteration.
        for (Student s : students.values()) {
            double avg = gm.calculateOverallAverage(s.getStudentId());
            int enrolledSubjects = gm.getEnrolledSubjectCount(s.getStudentId());
            s.displayStudentDetails(avg, enrolledSubjects);
            System.out.println("__________________________________________________________________________________");
        }
        System.out.println("\nTotal Students: " + students.size());
        System.out.printf("Average Class Grade: %.1f%%%n", getAverageClassGrade(gm));

        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine();
    }

    /**
     * Calculates the average grade across all students in the class.
     * Time Complexity: O(n) - Iterates through all students.
     *
     * @param gm The GradeManager instance.
     * @return The class-wide average grade percentage.
     */
    public double getAverageClassGrade(GradeManager gm) {
        if (students.isEmpty())
            return 0.0;

        double totalAverage = 0.0;
        for (Student s : students.values()) {
            totalAverage += gm.calculateOverallAverage(s.getStudentId());
        }
        return totalAverage / students.size();
    }

    /**
     * Retrieves students sorted by GPA in descending order.
     * Time Complexity: O(n log n) - Sorting keys in TreeMap.
     *
     * @param gm The GradeManager to calculate GPAs.
     * @return A map of GPA to list of students.
     */
    public java.util.Map<Double, List<Student>> getStudentsByGPA(GradeManager gm) {
        // TreeMap sorts by keys (GPA) in natural order (ascending)
        // We want descending, so we use Collections.reverseOrder()
        java.util.TreeMap<Double, List<Student>> gpaMap = new java.util.TreeMap<>(java.util.Collections.reverseOrder());

        for (Student s : students.values()) {
            double gpa = gm.calculateOverallAverage(s.getStudentId());
            gpaMap.computeIfAbsent(gpa, k -> new ArrayList<>()).add(s);
        }
        return gpaMap;
    }

    // Maintained for compatibility, but internally converts from Map
    public Student[] getAllStudents() {
        return students.values().toArray(new Student[0]);
    }

    public int getStudentCount() {
        return students.size();
    }
}
