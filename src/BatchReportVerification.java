
import services.BatchReportService;
import services.GradeManager;
import services.StudentManager;
import services.DataSeeder;
import models.Student;
import java.util.List;
import java.io.File;

public class BatchReportVerification {
    public static void main(String[] args) {
        System.out.println("Verifying Batch Report Service...");

        // Setup System
        StudentManager sm = new StudentManager();
        GradeManager gm = new GradeManager();
        DataSeeder.seedStudents(sm, gm);

        List<Student> students = sm.getAllStudents();
        System.out.println("Seeded " + students.size() + " students.");

        if (students.isEmpty()) {
            System.out.println("X Error: No students seeded.");
            return;
        }

        // Setup Service
        BatchReportService service = new BatchReportService();
        BatchReportService.BatchConfig config = new BatchReportService.BatchConfig(
                BatchReportService.ReportScope.ALL,
                BatchReportService.ReportFormat.TEXT,
                4,
                ""); // All students, text format, 4 threads

        System.out.println("Executing Batch Report generation...");
        long start = System.currentTimeMillis();

        try {
            service.executeBatch(students, gm, config);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("X Execution Failed");
            return;
        }

        long end = System.currentTimeMillis();
        System.out.println("Execution completed in " + (end - start) + "ms");

        // Verify Output Directory
        // The service logic uses a timestamped folder, e.g.,
        // "reports/batch_YYYY-MM-DD_HH-mm-ss/"
        // We can't predict the exact second easily for automation without modifying
        // service to return it.
        // But we can check if *any* batch report folder exists in "reports/"

        File reportDir = new File("reports");
        if (reportDir.exists() && reportDir.isDirectory()) {
            String[] files = reportDir.list();
            if (files != null && files.length > 0) {
                System.out.println("✓ Reports directory created and contains " + files.length + " batch runs.");
                System.out.println("  Last run: " + files[files.length - 1]);
            } else {
                System.out.println("! Reports directory exists but is empty.");
            }
        } else {
            System.out.println("X Reports directory not found.");
        }

        System.out.println("✓ Verification Script Finished");
    }
}
