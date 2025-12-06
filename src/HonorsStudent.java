public class HonorsStudent extends Student {
    private double passingGrade = 60.0;
    private boolean honorsEligible;

    public HonorsStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone, "Active");
        this.honorsEligible = false;
    }

    @Override
    public void displayStudentDetails(double currentAverage, int enrolledSubjects) {
        checkHonorsEligibility(currentAverage);
        String status = getStatus();
        if (honorsEligible) {
            status += " | Honors Eligible";
        }
        System.out.printf("%-8s | %-15s | %-10s | %-8.1f%% | %-10s%n",
                getStudentId(), getName(), getStudentType(), currentAverage, status);
        System.out.printf("         | Enrolled Subjects: %-2d | Passing Grade: %.0f%% | %s%n",
                enrolledSubjects, getPassingGrade(), honorsEligible ? "Honors Eligible" : "");
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
