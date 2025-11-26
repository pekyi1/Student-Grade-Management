public class StudentManager {
    private Student[] students;
    private int studentCount;

    public StudentManager() {
        this.students = new Student[50];
        this.studentCount = 0;
    }

    public void addStudent(Student student) {
        if (studentCount < students.length) {
            students[studentCount++] = student;
            System.out
                    .println("Student added successfully: " + student.getName() + " (" + student.getStudentId() + ")");
        } else {
            System.out.println("Cannot add student. Maximum capacity reached.");
        }
    }

    public Student findStudent(String studentId) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId().equals(studentId)) {
                return students[i];
            }
        }
        return null;
    }

    public void viewAllStudents(GradeManager gm) {
        if (studentCount == 0) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("\n--- Student List ---");
        for (int i = 0; i < studentCount; i++) {
            double avg = gm.calculateOverallAverage(students[i].getStudentId());
            students[i].displayStudentDetails(avg);
            System.out.println("--------------------");
        }
        System.out.println("Total Students: " + studentCount);
        System.out.println("Average Class Grade: " + getAverageClassGrade(gm));
    }

    public double getAverageClassGrade(GradeManager gm) {
        if (studentCount == 0)
            return 0.0;

        double totalAverage = 0.0;
        for (int i = 0; i < studentCount; i++) {
            totalAverage += gm.calculateOverallAverage(students[i].getStudentId());
        }
        return totalAverage / studentCount;
    }

    public int getStudentCount() {
        return studentCount;
    }
}
