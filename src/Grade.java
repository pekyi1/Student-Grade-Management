import java.time.LocalDate;

public class Grade implements Gradable {
    public static int gradeCounter;
    private String gradeID;
    private String studentID;
    private Subject subject;
    private double grade;
    private String date;

    public Grade(String studentID, Subject subject, double grade){
        // validate grade on creation; throw so callers (like App) can handle invalid input
        if (!validateGrade(grade)) {
            throw new IllegalArgumentException("Grade must be between 0 and 100.");
        }

        this.gradeID = "GRD" + gradeCounter++;
        this.studentID = studentID;
        this.subject = subject;
        this.grade = grade;
        this.date = LocalDate.now().toString();
    }

    @Override
    public boolean validateGrade(double grade){
        return grade >= 0 && grade <= 100;
    }

    @Override
    public void recordGrade(double grade){
        if(validateGrade(grade)){
            this.grade = grade;
        }else {
            System.out.println("Invalid grade entered. Grade not recorded");
        }
    }

    public String getGradeID(){
        return gradeID;
    }
    public String getStudentID(){
        return studentID;
    }
    public Subject getSubject(){
        return subject;
    }
    public double getGrade(){
        return grade;
    }
    public String getDate(){
        return date;
    }

    public void displayGradeDetails() {
        System.out.println("Grade ID: " + gradeID);
        System.out.println("Date: " + date);
        System.out.println("Subject: " + subject.getSubjectName() + " (" + subject.getSubjectType() + ")");
        System.out.println("Grade: " + grade);
        System.out.println("Letter Grade: " + getLetterGrade());
    }

    public String getLetterGrade(){
        if(grade >= 80.0){
            return "A";
        } else if (grade >= 60.0 && grade <= 79.9) {
            return "B";
        } else if (grade >= 50.0 && grade <= 59.9 ) {
            return "C";
        } else if (grade >= 40.0 && grade <= 49.0) {
            return "D";
        }
        return "F";
    }


}
