import java.util.List;

// This class provides utility methods for converting percentage grades to GPA scales
public class GPACalculator {

    // This method converts a percentage score to a 4.0 scale GPA
    public double convertToGPA(double percentage) {
        if (percentage >= 93)
            return 4.0;
        if (percentage >= 90)
            return 3.7;
        if (percentage >= 87)
            return 3.3;
        if (percentage >= 83)
            return 3.0;
        if (percentage >= 80)
            return 2.7;
        if (percentage >= 77)
            return 2.3;
        if (percentage >= 73)
            return 2.0;
        if (percentage >= 70)
            return 1.7;
        if (percentage >= 67)
            return 1.3;
        if (percentage >= 60)
            return 1.0;
        return 0.0;
    }

    // This method converts a percentage score to a letter grade
    public String getLetter(double percentage) {
        if (percentage >= 93)
            return "A";
        if (percentage >= 90)
            return "A-";
        if (percentage >= 87)
            return "B+";
        if (percentage >= 83)
            return "B";
        if (percentage >= 80)
            return "B-";
        if (percentage >= 77)
            return "C+";
        if (percentage >= 73)
            return "C";
        if (percentage >= 70)
            return "C-";
        if (percentage >= 67)
            return "D+";
        if (percentage >= 60)
            return "D";
        return "F";
    }

    // This method calculates the cumulative GPA from a list of grades
    public double calculateCumulativeGPA(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }
        double totalGPA = 0.0;
        for (Grade grade : grades) {
            totalGPA += convertToGPA(grade.getGrade());
        }
        return totalGPA / grades.size();
    }

    // This method generates a formatted string report of the student's GPA
    public String generateGPAReport(Student student, List<Grade> grades, int rank, int totalStudents,
            double overallAverage) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nGPA CALCULATION (4.0 Scale)\n\n");
        sb.append(String.format("%-14s | %-5s | %s%n", "Subject", "Grade", "GPA Points"));
        sb.append("-----------------------------------\n");

        for (Grade grade : grades) {
            double gpa = convertToGPA(grade.getGrade());
            String letter = getLetter(grade.getGrade());
            sb.append(String.format("%-14s | %-5.0f%% | %.1f (%s)%n",
                    grade.getSubject().getSubjectName(),
                    grade.getGrade(),
                    gpa,
                    letter));
        }

        double cumulativeGPA = calculateCumulativeGPA(grades);
        String letterGrade = getLetter(overallAverage);

        sb.append("\n\n");
        sb.append(String.format("Cumulative GPA: %.2f / 4.0%n", cumulativeGPA));
        sb.append("Letter Grade: " + letterGrade + "\n");
        sb.append("Class Rank: " + rank + " of " + totalStudents + "\n");

        sb.append("\nPerformance Analysis:\n");
        if (cumulativeGPA >= 3.5) {
            sb.append("Excellent performance (3.5+ GPA)\n");
        } else if (cumulativeGPA >= 3.0) {
            sb.append("Good performance (3.0+ GPA)\n");
        } else if (cumulativeGPA >= 2.0) {
            sb.append("Satisfactory performance (2.0+ GPA)\n");
        } else {
            sb.append("Needs improvement (Below 2.0 GPA)\n");
        }

        if (student instanceof HonorsStudent && cumulativeGPA >= 3.0) { // Assuming 3.0 is honors requirement for now,
                                                                        // or just generic message
            sb.append("Honors eligibility maintained\n");
        }

        return sb.toString();
    }
}
