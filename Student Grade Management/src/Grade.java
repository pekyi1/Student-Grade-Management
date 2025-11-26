import java.time.LocalDate;

public class Grade implements Gradable {
    private static int gradeCounter = 1;
    private String gradeId;
    private String studentId;
    private Subject subject;
    private double grade;
    private String date;

    public Grade(String studentId, Subject subject, double grade) {
        if (!validateGrade(grade)) {
            throw new IllegalArgumentException("Invalid grade. Must be between 0 and 100.");
        }
        this.gradeId = "G" + String.format("%03d", gradeCounter++);
        this.studentId = studentId;
        this.subject = subject;
        this.grade = grade;
        this.date = LocalDate.now().toString();
    }

    @Override
    public void recordGrade(double grade) {
        if (validateGrade(grade)) {
            this.grade = grade;
        } else {
            System.out.println("Invalid grade. Grade not recorded.");
        }
    }

    @Override
    public boolean validateGrade(double grade) {
        return grade >= 0 && grade <= 100;
    }

    public String getGradeId() {
        return gradeId;
    }

    public String getStudentId() {
        return studentId;
    }

    public Subject getSubject() {
        return subject;
    }

    public double getGrade() {
        return grade;
    }

    public String getDate() {
        return date;
    }

    public void displayGradeDetails() {
        System.out.println("Grade ID: " + gradeId);
        System.out.println("Date: " + date);
        System.out.println("Subject: " + subject.getSubjectName() + " (" + subject.getSubjectType() + ")");
        System.out.println("Grade: " + grade);
        System.out.println("Letter Grade: " + getLetterGrade());
    }

    public char getLetterGrade() {
        if (grade >= 90)
            return 'A';
        if (grade >= 80)
            return 'B';
        if (grade >= 70)
            return 'C';
        if (grade >= 60)
            return 'D';
        return 'F';
    }
}
