// This abstract class serves as the base for all student types
public abstract class Student implements Exportable {
    private String studentId;
    private String name;
    private int age;
    private String email;
    private String phone;
    private String status;
    private static int studentCounter = 1;

    public Student(String name, int age, String email, String phone, String status)
            throws exceptions.InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new exceptions.InvalidDataException("Student name cannot be empty.");
        }
        utils.ValidationUtils.validateAge(age);
        utils.ValidationUtils.validateEmail(email);
        utils.ValidationUtils.validatePhone(phone);

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

    // This method prints the full details of the student
    public abstract void displayStudentDetails(double currentAverage, int enrolledSubjects);

    public abstract String getStudentType();

    public abstract double getPassingGrade();

    // This method calculates the student's average grade using the grade manager
    public double calculateAverageGrade(GradeManager gm) {
        return gm.calculateOverallAverage(this.studentId);
    }

    // This method checks if the student's average meets the passing requirements
    public boolean isPassing(double currentAverage) { // This compares the current average with the passing grade and
                                                      // checks if the student meets the passing grade
        return currentAverage >= getPassingGrade();
    }

    // This method formats the student's data as a CSV string
    @Override
    public String toExportFormat() {
        return String.format("%s,%s,%s,%d,%s,%s,%s",
                studentId, name, getStudentType(), age, email, phone, status);
    }
}
