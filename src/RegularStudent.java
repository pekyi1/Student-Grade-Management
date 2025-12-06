public class RegularStudent extends Student {
    private final double passingGrade = 50.0;

    public RegularStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone, "Active");
    }

    @Override
    public void displayStudentDetails(double currentAverage, int enrolledSubjects) {
        System.out.printf("%-8s | %-15s | %-10s | %-8.1f%% | %-10s%n",
                getStudentId(), getName(), getStudentType(), currentAverage, getStatus());
        System.out.printf("         | Enrolled Subjects: %-2d | Passing Grade: %.0f%%%n",
                enrolledSubjects, getPassingGrade());
    }

    @Override
    public String getStudentType() {
        return "Regular";
    }

    @Override
    public double getPassingGrade() {
        return passingGrade;
    }

}
