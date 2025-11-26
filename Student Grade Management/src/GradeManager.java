public class GradeManager {
    private Grade[] grades;
    private int gradeCount;

    public GradeManager() {
        this.grades = new Grade[200];
        this.gradeCount = 0;
    }

    public void addGrade(Grade grade) {
        if (gradeCount < grades.length) {
            grades[gradeCount++] = grade;
            System.out.println("Grade recorded successfully: " + grade.getGradeId());
        } else {
            System.out.println("Cannot add grade. Maximum capacity reached.");
        }
    }

    public void viewGradesByStudent(String studentId) {
        boolean found = false;
        System.out.println("\n--- Grade Report for " + studentId + " ---");
        // Display in reverse chronological order
        for (int i = gradeCount - 1; i >= 0; i--) {
            if (grades[i].getStudentId().equals(studentId)) {
                grades[i].displayGradeDetails();
                System.out.println("--------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No grades recorded for this student.");
        } else {
            System.out.println("Core Average: " + calculateCoreAverage(studentId));
            System.out.println("Elective Average: " + calculateElectiveAverage(studentId));
            System.out.println("Overall Average: " + calculateOverallAverage(studentId));
        }
    }

    public double calculateCoreAverage(String studentId) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentId().equals(studentId) && grades[i].getSubject().getSubjectType().equals("Core")) {
                sum += grades[i].getGrade();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    public double calculateElectiveAverage(String studentId) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentId().equals(studentId)
                    && grades[i].getSubject().getSubjectType().equals("Elective")) {
                sum += grades[i].getGrade();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    public double calculateOverallAverage(String studentId) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < gradeCount; i++) {
            if (grades[i].getStudentId().equals(studentId)) {
                sum += grades[i].getGrade();
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

    public int getGradeCount() {
        return gradeCount;
    }
}
