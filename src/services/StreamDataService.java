package services;

import models.Grade;
import models.Student;
import services.GradeManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamDataService {

    /**
     * Filters students based on a given predicate criteria.
     * Example: students with GPA > 3.5
     */
    public List<Student> filterStudents(List<Student> students, Predicate<Student> criteria) {
        return students.stream()
                .filter(criteria)
                .collect(Collectors.toList());
    }

    /**
     * Extracts a list of email addresses from a list of students.
     */
    public List<String> extractEmails(List<Student> students) {
        return students.stream()
                .map(Student::getEmail)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the average grade per subject.
     */
    public Map<String, Double> calculateAverageGradePerSubject(List<Grade> grades) {
        return grades.stream()
                .collect(Collectors.groupingBy(
                        grade -> grade.getSubject().getSubjectName(),
                        Collectors.averagingDouble(Grade::getGrade)));
    }

    /**
     * Groups students by grade range (A, B, C, etc.) based on their overall
     * average.
     */
    public Map<String, List<Student>> groupStudentsByGradeRange(List<Student> students, GradeManager gradeManager) {
        return students.stream()
                .collect(Collectors.groupingBy(student -> {
                    double avg = gradeManager.calculateOverallAverage(student.getStudentId());
                    if (avg >= 90)
                        return "A (90-100)";
                    if (avg >= 80)
                        return "B (80-89)";
                    if (avg >= 70)
                        return "C (70-79)";
                    if (avg >= 60)
                        return "D (60-69)";
                    return "F (0-59)";
                }));
    }

    /**
     * Finds the top N students based on their overall average grade.
     */
    public List<Student> findTopStudents(List<Student> students, GradeManager gradeManager, int limit) {
        return students.stream()
                .sorted((s1, s2) -> Double.compare(
                        gradeManager.calculateOverallAverage(s2.getStudentId()),
                        gradeManager.calculateOverallAverage(s1.getStudentId())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Calculates total grades grouped by subject and student type (Advanced
     * Grouping).
     */
    public Map<String, Map<String, DoubleSummaryStatistics>> analyzeGradesBySubjectAndType(List<Grade> grades) {
        return grades.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getSubject().getSubjectName(),
                        Collectors.groupingBy(
                                g -> g.getSubject().getSubjectType(),
                                Collectors.summarizingDouble(Grade::getGrade))));
    }

    /**
     * Streams a file line by line to count lines matching a keyword (Memory
     * Efficient).
     */
    public long countLinesWithKeyword(Path path, String keyword) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.filter(line -> line.contains(keyword)).count();
        }
    }

    /**
     * Compares performance of Sequential vs Parallel stream processing.
     * Returns a formatted result string.
     */
    public String comparePerformance(List<Student> largeDataset) {
        long startTimeSeq = System.nanoTime();
        long countSeq = largeDataset.stream()
                .filter(s -> s.getAge() > 20)
                .map(Student::getName)
                .sorted()
                .count();
        long durationSeq = (System.nanoTime() - startTimeSeq) / 1000; // microseconds

        long startTimePar = System.nanoTime();
        long countPar = largeDataset.parallelStream()
                .filter(s -> s.getAge() > 20)
                .map(Student::getName)
                .sorted()
                .count();
        long durationPar = (System.nanoTime() - startTimePar) / 1000; // microseconds

        return String.format(
                "Sequential Stream: %d µs (Count: %d)%n" +
                        "Parallel Stream:   %d µs (Count: %d)%n" +
                        "Speedup:           %.2fx",
                durationSeq, countSeq, durationPar, countPar, (double) durationSeq / durationPar);
    }
}
