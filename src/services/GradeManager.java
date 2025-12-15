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
    // US-1: Use LinkedList<Grade> for frequent insertions/deletions in grade
    // history
    private java.util.LinkedList<Grade> grades;
    // US-8: Thread-Safe Caching System
    private final CacheService<String, Double> averageCache = new CacheService<>(150);

    public CacheService<String, Double> getCacheService() {
        return averageCache;
    }

    public GradeManager() {
        this.grades = new java.util.LinkedList<>();
    }

    /**
     * Adds a grade to the system after validating it.
     * Time Complexity: O(1) - Adding to end of LinkedList.
     * (Duplicate check is O(n))
     *
     * @param grade The Grade object to be added.
     * @throws InvalidGradeException If the grade value is not between 0 and 100.
     * @throws InvalidDataException  If the data is invalid.
     */
    public void addGrade(Grade grade) throws InvalidGradeException, InvalidDataException {
        if (grade.getGrade() < 0 || grade.getGrade() > 100) {
            throw new InvalidGradeException(grade.getGrade());
        }

        // Check for existing grade for this student and subject - O(n)
        for (Grade g : grades) {
            if (g.getStudentID().equals(grade.getStudentID()) &&
                    g.getSubject().equals(grade.getSubject())) {

                // Update existing grade
                g.recordGrade(grade.getGrade());
                // Invalidate cache
                averageCache.remove(grade.getStudentID());
                System.out.println("\n✓ Grade updated successfully!");
                return;
            }
        }

        // LinkedList has no fixed capacity, but for safety in this project scope:
        // (Removed capacity check as Lists grow dynamically)

        grades.add(grade);
        // Invalidate cache
        averageCache.remove(grade.getStudentID());

        if (auditService != null) {
            auditService.log("RECORD_GRADE", "Grade: " + grade.getGrade() + " for " + grade.getStudentID(), "SYSTEM",
                    true);
        }

        System.out.println("\n✓ Grade recorded successfully!");
    }

    private AuditLogService auditService;

    public void setAuditService(AuditLogService service) {
        this.auditService = service;
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
        // LinkedList descendingIterator is efficient
        java.util.Iterator<Grade> it = grades.descendingIterator();
        while (it.hasNext()) {
            Grade g = it.next();
            if (g.getStudentID().equals(studentId)) {
                System.out.printf("%-8s | %-12s | %-15s | %-10s | %-8.1f%%%n",
                        g.getGradeID(),
                        g.getDate(),
                        g.getSubject().getSubjectName(),
                        g.getSubject().getSubjectType(),
                        g.getGrade());
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


    }

    private boolean checkAllCoreSubjectsPassing(String studentId, double passingGrade) {
        for (Grade g : grades) {
            if (g.getStudentID().equals(studentId) &&
                    g.getSubject().getSubjectType().equals("Core")) {
                if (g.getGrade() < passingGrade) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Calculates the average of all core subject grades for a student.
     * Time Complexity: O(n)
     */
    public double calculateCoreAverage(String studentId) {
        double sum = 0;
        int count = 0;
        for (Grade g : grades) {
            if (g.getStudentID().equals(studentId) && g.getSubject().getSubjectType().equals("Core")) {
                sum += g.getGrade();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    /**
     * Calculates the average of all elective subject grades for a student.
     * Time Complexity: O(n)
     */
    public double calculateElectiveAverage(String studentId) {
        double sum = 0;
        int count = 0;
        for (Grade g : grades) {
            if (g.getStudentID().equals(studentId)
                    && g.getSubject().getSubjectType().equals("Elective")) {
                sum += g.getGrade();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    /**
     * Calculates the overall average of all grades for a student.
     * Time Complexity: O(n) without cache, O(1) with cache hit.
     */
    public double calculateOverallAverage(String studentId) {
        // Check cache first
        Double cachedAvg = averageCache.get(studentId);
        if (cachedAvg != null) {
            return cachedAvg;
        }

        double sum = 0;
        int count = 0;
        for (Grade g : grades) {
            if (g.getStudentID().equals(studentId)) {
                sum += g.getGrade();
                count++;
            }
        }
        double avg = count == 0 ? 0.0 : sum / count;

        // Update cache
        averageCache.put(studentId, avg);

        return avg;
    }

    // This method counts how many subjects a student has received grades for
    public int getEnrolledSubjectCount(String studentId) {
        int count = 0;
        for (Grade g : grades) {
            if (g.getStudentID().equals(studentId)) {
                count++;
            }
        }
        return count;
    }

    // This method retrieves a list of all grades belonging to a specific student
    public List<Grade> getGradesForStudent(String studentId) {
        List<Grade> studentGrades = new ArrayList<>();
        for (Grade g : grades) {
            if (g.getStudentID().equals(studentId)) {
                studentGrades.add(g);
            }
        }
        return studentGrades;
    }

    /**
     * Determines a student's rank in the class based on their overall average.
     * Time Complexity: O(n) - Iterates all grades to build averages.
     *
     * @param studentId The ID of the student to rank.
     * @return The student's rank (1-based), or -1 if the student has no grades.
     */
    public int calculateClassRank(String studentId) {
        Map<String, Double> studentAverages = new HashMap<>();
        // Calculate average for all students who have grades
        for (Grade g : grades) {
            String id = g.getStudentID();
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
        for (Grade g : grades) {
            studentsWithGrades.add(g.getStudentID());
        }
        return studentsWithGrades.size();
    }

    /**
     * Retrieves a list of all grades recorded in the system.
     *
     * @return A list of all Grade objects.
     */
    public List<Grade> getAllGrades() {
        return new ArrayList<>(grades);
    }

    /**
     * Refreshes the cache for all students.
     * Useful for background tasks to keep cache warm.
     */
    public void refreshAllCache(StudentManager studentManager) {
        System.out.println("Refreshing Grade Cache...");
        for (Student s : studentManager.getAllStudents()) {
            calculateOverallAverage(s.getStudentId()); // This updates the cache
        }
    }

    /**
     * Retrieves a list of all unique subjects currently recorded in the system.
     * Time Complexity: O(n)
     *
     * @return A list of unique Subject objects.
     */
    public List<models.Subject> getAllUniqueSubjects() {
        // US-1: Use HashSet<String> for tracking unique courses enrolled.
        // Even though we return a List, we use Set for uniqueness.
        Set<models.Subject> subjects = new HashSet<>();
        for (Grade g : grades) {
            subjects.add(g.getSubject());
        }
        return new ArrayList<>(subjects);
    }
}