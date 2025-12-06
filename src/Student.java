public abstract class Student {
    private String studentId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String status;
    private static int studentCounter = 1;

    public Student(String name, int age, String email, String phone, String status) {
        this.studentId = String.format("STU%03d", studentCounter++);
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract void displayStudentDetails(double currentAverage, int enrolledSubjects);

    public abstract String getStudentType();

    public abstract double getPassingGrade();

    public double calculateAverageGrade(GradeManager gm) {
        return gm.calculateOverallAverage(this.studentId);
    }

    public boolean isPassing(double currentAverage) { // This compares the current average with the passing grade and
                                                      // checks if the student meets the passing grade
        return currentAverage >= getPassingGrade();
    }
}
