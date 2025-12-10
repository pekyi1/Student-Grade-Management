package services;

import exceptions.InvalidDataException;
import exceptions.InvalidGradeException;
import models.Grade;
import models.Student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// This class handles the storage and calculation of grades for all students
public class GradeManager {
    private Grade[] grades;
    private int gradeCount;

    public GradeManager() {
        this.grades = new Grade[200];
        this.gradeCount = 0;
    }

    /**
     * Adds a grade to the system after validating it.
     *
     * @param grade The Grade object to be added.
     * @throws InvalidGradeException If the grade value is not between 0 and 100.
     * @throws InvalidDataException  If the storage capacity has been reached.
     */
    public void addGrade(Grade grade) throws InvalidGradeException, InvalidDataException {
        if (gradeCount >= grades.length) {
            throw new InvalidDataException("Cannot add grade. Maximum capacity reached.");
        }
        if (grade.getGrade() < 0 || grade.getGrade() > 100) {
            throw new InvalidGradeException(grade.getGrade());
        }
        grades[gradeCount++] = grade;
        System.out.println("\n✓ Grade recorded successfully!");
    }

    // This method prints a detailed grade history for a specific student
    public void viewGradesByStudent(Student student) {
        String studentId = student.getStudentId();
        boolean found = false;

        System.out.println("\nStudent: " + studentId + " - " + student.getName());
        System.out.println("Type: " + student.getStudentType() + " Student");
        double currentAverage = calculateOverallAverage(studentId);
        System.out.printf("Current Average: %.1f%%%n", currentAverage);
        System.out.println("Status: " + (student.isPassing(currentAverage) ? "PASSING ✓" : "FAILING ✗"));

        System.out.println("\nGRADE HISTORY");
        System.out.println("__________________________________________________________________________________");
        System.out.printf("%-8s | %-12s | %-15s | %-10s | %-8s%n", "GRD ID", "DATE", "SUBJECT", "TYPE", "GRADE");
        System.out.println("__________________________________________________________________________________");

        // Display in reverse chronological order
        for (int i = gradeCount - 1; i >= 0; i--) {
            if (grades[i].getStudentID().equals(studentId)) {
                System.out.printf("%-8s | %-12s | %-15s | %-10s | %-8.1f%%%n",
                        grades[i].getGradeID(),
                        grades[i].getDate(),
                        grades[i].getSubject().getSubjectName(),
                        grades[i].getSubject().getSubjectType(),
                        grades[i].getGrade());
                found = true;
            }
        }
        System.out.println("__________________________________________________________________________________");

        if (!found) {
            System.out.println("No grades recorded for this student.");
        } else {
            System.out.println("\nTotal Grades: " + getEnrolledSubjectCount(studentId));
            System.out.printf("Core Subjects Average: %.1f%%%n", calculateCoreAverage(studentId));
            System.out.printf("Elective Subjects Average: %.1f%%%n", calculateElectiveAverage(studentId));
            System.out.printf("Overall Average: %.1f%%%n", currentAverage);

            System.out.println("\nPerformance Summary:");
            boolean allCorePassing = checkAllCoreSubjectsPassing(studentId, student.getPassingGrade());
            System.out.println((allCorePassing ? "Yes: " : "No: ") + " Passing all core subjects");
            System.out.println(
                    (student.isPassing(currentAverage) ? "Yes: " : "No: ") + " Meeting passing grade requirement ("
                            + (int) student.getPassingGrade() + "%)");
        }

        System.out.println("\nPress Enter to continue...");
        new java.util.Scanner(System.in).nextLine();
    }

    private boolean checkAllCoreSubjectsPassing(String studentId, double passingGrade) {
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentID().equals(studentId) &&
                    grades[i].getSubject().getSubjectType().equals("Core")) {
                if (grades[i].getGrade() < passingGrade) {
                    return false;
                }
            }
        }
        return true;
    }

    // This method calculates the average of all core subject grades for a student
    public double calculateCoreAverage(String studentId) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentID().equals(studentId) && grades[i].getSubject().getSubjectType().equals("Core")) {
                sum += grades[i].getGrade();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    // This method calculates the average of all elective subject grades for a
    // student
    public double calculateElectiveAverage(String studentId) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentID().equals(studentId)
                    && grades[i].getSubject().getSubjectType().equals("Elective")) {
                sum += grades[i].getGrade();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    // This method calculates the overall average of all grades for a student
    public double calculateOverallAverage(String studentId) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentID().equals(studentId)) {
                sum += grades[i].getGrade();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    // This method counts how many subjects a student has received grades for
    public int getEnrolledSubjectCount(String studentId) {
        int count = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentID().equals(studentId)) {
                count++;
            }
        }
        return count;
    }

    // This method retrieves a list of all grades belonging to a specific student
    public List<Grade> getGradesForStudent(String studentId) {
        List<Grade> studentGrades = new ArrayList<>();
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentID().equals(studentId)) {
                studentGrades.add(grades[i]);
            }
        }
        return studentGrades;
    }

    /**
     * Determines a student's rank in the class based on their overall average.
     *
     * @param studentId The ID of the student to rank.
     * @return The student's rank (1-based), or -1 if the student has no grades.
     */
    public int calculateClassRank(String studentId) {
        Map<String, Double> studentAverages = new HashMap<>();
        // Calculate average for all students who have grades
        for (int i = 0; i < gradeCount; i++) {
            String id = grades[i].getStudentID();
            if (!studentAverages.containsKey(id)) {
                studentAverages.put(id, calculateOverallAverage(id));
            }
        }

        if (!studentAverages.containsKey(studentId)) {
            return -1;
        }

        double targetAverage = calculateOverallAverage(studentId);
        int rank = 1;
        for (double avg : studentAverages.values()) {
            if (avg > targetAverage) {
                rank++;
            }
        }
        return rank;
    }

    /**
     * Counts the total number of unique students who have recorded grades.
     *
     * @return The count of students with at least one grade.
     */
    public int getTotalStudentsWithGrades() {
        Set<String> studentsWithGrades = new HashSet<>();
        for (int i = 0; i < gradeCount; i++) {
            studentsWithGrades.add(grades[i].getStudentID());
        }
        return studentsWithGrades.size();
    }

    /**
     * Retrieves a list of all grades recorded in the system.
     *
     * @return A list of all Grade objects.
     */
    public List<Grade> getAllGrades() {
        List<Grade> allGrades = new ArrayList<>();
        for (int i = 0; i < gradeCount; i++) {
            allGrades.add(grades[i]);
        }
        return allGrades;
    }
}