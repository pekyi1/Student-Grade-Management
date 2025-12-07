import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// This class calculates various statistical metrics for the class grades
public class ClassStatistics {

    // This method generates a comprehensive statistical report for all class grades
    public String generateClassStatisticsReport(List<Grade> allGrades, List<Student> allStudents) {
        if (allGrades == null || allGrades.isEmpty()) {
            return "No grades available for statistics.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\nCLASS STATISTICS\n");
        sb.append("__________________________________________________\n\n");

        sb.append("Total Students: " + allStudents.size() + "\n");
        sb.append("Total Grades Recorded: " + allGrades.size() + "\n\n");

        // Grade Distribution
        sb.append("GRADE DISTRIBUTION\n");
        sb.append("__________________________________________________\n");
        appendGradeDistribution(sb, allGrades);

        // Statistical Analysis
        List<Double> gradeValues = allGrades.stream().map(Grade::getGrade).sorted().collect(Collectors.toList());
        sb.append("\nSTATISTICAL ANALYSIS\n");
        sb.append("__________________________________________________\n");
        sb.append(String.format("Mean (Average):      %.1f%%%n", calculateMean(gradeValues)));
        sb.append(String.format("Median:              %.1f%%%n", calculateMedian(gradeValues)));
        sb.append(String.format("Mode:                %.1f%%%n", calculateMode(gradeValues)));
        sb.append(String.format("Standard Deviation:  %.1f%n", calculateStandardDeviation(gradeValues)));
        sb.append(String.format("Range:               %.1f%% (%.0f%% - %.0f%%)%n",
                gradeValues.get(gradeValues.size() - 1) - gradeValues.get(0),
                gradeValues.get(0),
                gradeValues.get(gradeValues.size() - 1)));

        // High/Low
        Grade highest = allGrades.stream().max((g1, g2) -> Double.compare(g1.getGrade(), g2.getGrade())).orElse(null);
        Grade lowest = allGrades.stream().min((g1, g2) -> Double.compare(g1.getGrade(), g2.getGrade())).orElse(null);

        if (highest != null) {
            sb.append(String.format("\nHighest Grade:       %.0f%% (%s - %s)%n",
                    highest.getGrade(), highest.getStudentID(), highest.getSubject().getSubjectName()));
        }
        if (lowest != null) {
            sb.append(String.format("Lowest Grade:        %.0f%% (%s - %s)%n",
                    lowest.getGrade(), lowest.getStudentID(), lowest.getSubject().getSubjectName()));
        }

        // Subject Performance
        sb.append("\nSUBJECT PERFORMANCE\n");
        sb.append("__________________________________________________\n");
        appendSubjectPerformance(sb, allGrades);

        // Student Type Comparison
        sb.append("\nSTUDENT TYPE COMPARISON\n");
        sb.append("__________________________________________________\n");
        appendStudentTypeComparison(sb, allGrades, allStudents);

        return sb.toString();
    }

    private void appendGradeDistribution(StringBuilder sb, List<Grade> grades) {
        int[] counts = new int[5]; // A, B, C, D, F
        for (Grade g : grades) {
            double val = g.getGrade();
            if (val >= 90)
                counts[0]++;
            else if (val >= 80)
                counts[1]++;
            else if (val >= 70)
                counts[2]++;
            else if (val >= 60)
                counts[3]++;
            else
                counts[4]++;
        }
        String[] labels = { "90-100% (A):", "80-89%  (B):", "70-79%  (C):", "60-69%  (D):", "0-59%   (F):" };
        int total = grades.size();

        for (int i = 0; i < 5; i++) {
            double percent = (double) counts[i] / total * 100;
            int barLength = (int) (percent / 2); // Scale down for display
            String bar = repeatString("█", barLength) + repeatString("░", 50 - barLength);
            sb.append(String.format("%-15s %s %.1f%% (%d grades)%n", labels[i], bar, percent, counts[i]));
        }
    }

    // Helper method for Java 8 compatibility
    private String repeatString(String str, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    // This method calculates the arithmetic mean of a list of values
    private double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    // This method finds the median value in a list of numbers
    private double calculateMedian(List<Double> values) {
        int size = values.size();
        if (size == 0)
            return 0.0;
        if (size % 2 == 0) {
            return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        } else {
            return values.get(size / 2);
        }
    }

    // This method finds the most frequent value (mode) in a list
    private double calculateMode(List<Double> values) {
        Map<Double, Integer> counts = new HashMap<>();
        for (Double v : values) {
            counts.put(v, counts.getOrDefault(v, 0) + 1);
        }
        double mode = values.get(0);
        int maxCount = 0;
        for (Map.Entry<Double, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mode = entry.getKey();
            }
        }
        return mode;
    }

    // This method calculates the standard deviation of a list of values
    private double calculateStandardDeviation(List<Double> values) {
        double mean = calculateMean(values);
        double sumSquaredDiff = values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).sum();
        return Math.sqrt(sumSquaredDiff / values.size());
    }

    private void appendSubjectPerformance(StringBuilder sb, List<Grade> grades) {
        Map<String, List<Double>> subjectGrades = new HashMap<>();
        Map<String, String> subjectTypes = new HashMap<>();

        for (Grade g : grades) {
            String name = g.getSubject().getSubjectName();
            subjectGrades.computeIfAbsent(name, k -> new ArrayList<>()).add(g.getGrade());
            subjectTypes.put(name, g.getSubject().getSubjectType());
        }

        // Separate Core and Elective
        sb.append("Core Subjects:\n");
        subjectGrades.forEach((name, list) -> {
            if (subjectTypes.get(name).equalsIgnoreCase("Core")) {
                double avg = list.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                sb.append(String.format("  %-15s: %.1f%%%n", name, avg));
            }
        });

        sb.append("\nElective Subjects:\n");
        subjectGrades.forEach((name, list) -> {
            if (subjectTypes.get(name).equalsIgnoreCase("Elective")) {
                double avg = list.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                sb.append(String.format("  %-15s: %.1f%%%n", name, avg));
            }
        });
    }

    private void appendStudentTypeComparison(StringBuilder sb, List<Grade> grades, List<Student> students) {
        Map<String, String> studentTypes = students.stream()
                .collect(Collectors.toMap(Student::getStudentId, Student::getStudentType));

        List<Double> regularGrades = new ArrayList<>();
        List<Double> honorsGrades = new ArrayList<>();

        for (Grade g : grades) {
            String type = studentTypes.get(g.getStudentID());
            if (type != null) {
                if (type.equalsIgnoreCase("Regular")) {
                    regularGrades.add(g.getGrade());
                } else if (type.equalsIgnoreCase("Honors")) {
                    honorsGrades.add(g.getGrade());
                }
            }
        }

        double regularAvg = regularGrades.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double honorsAvg = honorsGrades.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        long regularCount = students.stream().filter(s -> s.getStudentType().equalsIgnoreCase("Regular")).count();
        long honorsCount = students.stream().filter(s -> s.getStudentType().equalsIgnoreCase("Honors")).count();

        sb.append(String.format("Regular Students:    %.1f%% average (%d students)%n", regularAvg, regularCount));
        sb.append(String.format("Honors Students:     %.1f%% average (%d students)%n", honorsAvg, honorsCount));
    }
}
