public abstract class Student {
    private String studentId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String status;
    private static int studentCounter = 1;

    public Student(String name, int age, String email, String phone) {
        this.studentId = "S" + String.format("%03d", studentCounter++);
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.status = "Active";
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

    public abstract void displayStudentDetails(double currentAverage);

    public abstract String getStudentType();

    public abstract double getPassingGrade();

    public double calculateAverageGrade(GradeManager gm) {
        return gm.calculateOverallAverage(this.studentId);
    }

    public boolean isPassing(double currentAverage) {
        return currentAverage >= getPassingGrade();
    }
}
