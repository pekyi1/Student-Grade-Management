import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private Student student;
    private GradeManager gradeManager;

    @BeforeEach
    public void setUp() {
        reportGenerator = new ReportGenerator();
        gradeManager = new GradeManager();
        Subject math = new CoreSubject("Math", "MTH101");
        Subject english = new CoreSubject("English", "ENG101");

        try {
            student = new RegularStudent("Alice", 20, "alice@test.com", "1234567890");
            gradeManager.addGrade(new Grade(student.getStudentId(), math, 90.0));
            gradeManager.addGrade(new Grade(student.getStudentId(), english, 85.0));
        } catch (Exception e) {
            fail("Setup failed: " + e.getMessage());
        }
    }

    @Test
    public void testGenerateSummaryReport() {
        String report = reportGenerator.generateSummaryReport(student, gradeManager);

        assertNotNull(report);
        assertTrue(report.contains("STUDENT GRADE REPORT - SUMMARY"));
        assertTrue(report.contains("Alice"));
        assertTrue(report.contains("Overall Average"));
        // 90 + 85 / 2 = 87.5
        assertTrue(report.contains("87.5%"));
    }

    @Test
    public void testGenerateDetailedReport() {
        String report = reportGenerator.generateDetailedReport(student, gradeManager);

        assertNotNull(report);
        assertTrue(report.contains("STUDENT GRADE REPORT - DETAILED"), "Report missing header");
        assertTrue(report.contains("Math"), "Report missing subject Math");
        assertTrue(report.contains("90.0"), "Report missing grade 90.0. Actual report:\n" + report);
        assertTrue(report.contains("English"), "Report missing subject English");
        assertTrue(report.contains("85.0"), "Report missing grade 85.0");
    }

    @Test
    public void testGenerateBothReport() {
        String report = reportGenerator.generateBothReport(student, gradeManager);

        assertNotNull(report);
        assertTrue(report.contains("SUMMARY"));
        assertTrue(report.contains("DETAILED"));
        assertTrue(report.contains("87.5%"));
        assertTrue(report.contains("Math"));
    }
}
