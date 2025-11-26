public class HonorsStudent extends Student {
    private final double passingGrade = 60.0;
    private boolean honorsEligible;

    public HonorsStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
        this.honorsEligible = false; // Default
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
        checkHonorsEligibility(currentAverage);
        System.out.println("Honors Eligible: " + (honorsEligible ? "Yes" : "No"));
    }

    @Override
    public String getStudentType() {
        return "Honors";
    }

    @Override
    public double getPassingGrade() {
        return passingGrade;
    }

    public boolean checkHonorsEligibility(double currentAverage) {
        if (currentAverage >= 85.0) {
            honorsEligible = true;
        } else {
            honorsEligible = false;
        }
        return honorsEligible;
    }
}
