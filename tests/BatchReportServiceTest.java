
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import services.BatchReportService;
import services.GradeManager;
import models.Student;
import models.RegularStudent;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class BatchReportServiceTest {

    @Test
    public void testGenerateBatchReports() {
        // Setup
        GradeManager gm = new GradeManager();
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            try {
                // Constructor: Name, Age, Email, Phone, EnrollmentDate
                students.add(new RegularStudent(
                        "Student " + (char) ('A' + i),
                        20,
                        "student" + i + "@example.com",
                        "123-456-7890",
                        "2024-01-01"));
            } catch (Exception e) {
                fail("Failed to create student: " + e.getMessage());
            }
        }

        BatchReportService service = new BatchReportService();
        BatchReportService.BatchConfig config = new BatchReportService.BatchConfig(
                BatchReportService.ReportScope.ALL,
                BatchReportService.ReportFormat.TEXT,
                4,
                "");

        // Execute with 4 threads
        assertDoesNotThrow(() -> service.executeBatch(students, gm, config));
    }

    @Test
    public void testGenerateReportsEmptyList() {
        BatchReportService service = new BatchReportService();
        BatchReportService.BatchConfig config = new BatchReportService.BatchConfig(
                BatchReportService.ReportScope.ALL,
                BatchReportService.ReportFormat.TEXT,
                2,
                "");
        assertDoesNotThrow(() -> service.executeBatch(new ArrayList<>(), new GradeManager(), config));
    }
}
