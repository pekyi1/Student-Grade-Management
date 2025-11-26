public class RegularStudent extends Student {
    private final double passingGrade = 50.0;

    public RegularStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    @Override
    public void displayStudentDetails(double currentAverage) {
        System.out.println("Student ID: " + getStudentId());
        System.out.println("Name: " + getName());
        System.out.println("Type: " + getStudentType());
        System.out.println("Status: " + getStatus());
        System.out.println("Average Grade: " + currentAverage);
        System.out.println("Passing: " + (isPassing(currentAverage) ? "Yes" : "No"));
        System.out.println("Passing Grade: " + getPassingGrade());
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
