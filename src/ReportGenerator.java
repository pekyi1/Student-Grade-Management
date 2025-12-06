public class ReportGenerator {

    public String generateSummaryReport(Student student, GradeManager gm) {
        StringBuilder sb = new StringBuilder();
        String studentId = student.getStudentId();
        double overallAvg = gm.calculateOverallAverage(studentId);

        sb.append("STUDENT GRADE REPORT - SUMMARY\n");
        sb.append("__________________________________________________\n");
        sb.append("Student: ").append(studentId).append(" ").append(student.getName()).append("\n");
        sb.append("Type: ").append(student.getStudentType()).append("\n");
        sb.append("Status: ").append(student.getStatus()).append("\n");
        sb.append("__________________________________________________\n\n");

        sb.append("PERFORMANCE SUMMARY\n");
        sb.append("Total Grades: ").append(gm.getEnrolledSubjectCount(studentId)).append("\n");
        sb.append(String.format("Core Subjects Average: %.1f%%%n", gm.calculateCoreAverage(studentId)));
        sb.append(String.format("Elective Subjects Average: %.1f%%%n", gm.calculateElectiveAverage(studentId)));
        sb.append(String.format("Overall Average: %.1f%%%n", overallAvg));
        sb.append("Passing: ").append(student.isPassing(overallAvg) ? "Yes" : "No").append("\n");

        return sb.toString();
    }

    public String generateDetailedReport(Student student, GradeManager gm) {
        StringBuilder sb = new StringBuilder();
        String studentId = student.getStudentId();

        sb.append("STUDENT GRADE REPORT - DETAILED\n");
        sb.append("__________________________________________________\n");
        sb.append("Student: ").append(studentId).append(" ").append(student.getName()).append("\n");
        sb.append("__________________________________________________\n\n");

        appendGradeList(sb, gm.getGradesForStudent(studentId));

        return sb.toString();
    }

    public String generateBothReport(Student student, GradeManager gm) {
        StringBuilder sb = new StringBuilder();
        String studentId = student.getStudentId();

        sb.append(generateSummaryReport(student, gm));
        sb.append("\n__________________________________________________\n");
        appendGradeList(sb, gm.getGradesForStudent(studentId));

        return sb.toString();
    }

    private void appendGradeList(StringBuilder sb, java.util.List<Grade> grades) {
        sb.append("DETAILED GRADE HISTORY\n");
        sb.append(String.format("%-12s | %-15s | %-10s | %-8s%n", "DATE", "SUBJECT", "TYPE", "GRADE"));
        sb.append("--------------------------------------------------\n");

        for (Grade grade : grades) {
            sb.append(String.format("%-12s | %-15s | %-10s | %-8.1f%%%n",
                    grade.getDate(),
                    grade.getSubject().getSubjectName(),
                    grade.getSubject().getSubjectType(),
                    grade.getGrade()));
        }
        sb.append("__________________________________________________\n");
    }
}
