
import services.StudentManager;
import services.GradeManager;
import services.DataSeeder;
import models.Student;
import models.Grade;
import java.util.List;

public class SeederVerification {
    public static void main(String[] args) {
        System.out.println("Verifying Data Seeder...");
        StudentManager sm = new StudentManager();
        GradeManager gm = new GradeManager();

        // Run Seeder
        DataSeeder.seedStudents(sm, gm);

        List<Student> students = sm.getAllStudents();
        int studentsWithGrades = 0;

        for (Student s : students) {
            List<Grade> grades = gm.getGradesForStudent(s.getStudentId());
            if (!grades.isEmpty()) {
                studentsWithGrades++;
                int coreCount = 0;
                int electiveCount = 0;

                for (Grade g : grades) {
                    if (g.getSubject().getSubjectType().equalsIgnoreCase("Core")) {
                        coreCount++;
                    } else if (g.getSubject().getSubjectType().equalsIgnoreCase("Elective")) {
                        electiveCount++;
                    }
                }

                if (studentsWithGrades <= 5) {
                    System.out.println("  Student " + s.getStudentId() + ": " + grades.size() + " grades (Core: "
                            + coreCount + ", Elective: " + electiveCount + ")");
                }

                if (coreCount < 4) {
                    System.out.println("  X Error: Missing core subjects for " + s.getStudentId());
                }
                if (electiveCount < 2) {
                    System.out.println("  X Error: Missing elective subjects for " + s.getStudentId());
                }
            }
        }

        System.out.println("Total Students with Grades: " + studentsWithGrades);

        if (studentsWithGrades >= 25) {
            System.out.println("✓ Verification Passed: At least 25 students have seeded grades.");
        } else {
            System.out.println("X Verification Failed: Expected 25, found " + studentsWithGrades);
        }
    }
}
