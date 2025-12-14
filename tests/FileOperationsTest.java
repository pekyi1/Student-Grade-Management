
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import services.FileExporter;
import services.StudentManager;
import services.GradeManager;
import services.BulkImportService;
import models.Student;
import models.RegularStudent;
import models.Grade;
import models.Subject;
import services.SubjectFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileOperationsTest {

    private static final String IMPORT_DIR = "imports";

    private FileExporter exporter;

    @BeforeEach
    public void setup() {
        exporter = new FileExporter(); // This creates dirs
    }

    @AfterEach
    public void cleanup() {
        // Cleanup test files if desired, or leave for manual inspection
    }

    @Test
    public void testExportToCSV() throws Exception {
        Student s = new RegularStudent("Test User", 20, "test@test.com", "1234567890", "2024-01-01");
        String filename = "test_export";
        String path = exporter.exportToCSV(s, filename);

        Path filePath = Paths.get(path);
        assertTrue(Files.exists(filePath), "CSV file should exist");
        assertTrue(filePath.toString().endsWith(".csv"), "File should end with .csv");

        String content = new String(Files.readAllBytes(filePath));
        assertTrue(content.contains("Test User"), "CSV content should contain student name");
    }

    @Test
    public void testExportToJSON() throws Exception {
        Student s = new RegularStudent("Json User", 21, "json@test.com", "0987654321", "2024-01-01");
        String filename = "test_json";
        String path = exporter.exportToJSON(s, filename);

        Path filePath = Paths.get(path);
        assertTrue(Files.exists(filePath), "JSON file should exist");
        assertTrue(filePath.toString().endsWith(".json"), "File should end with .json");

        String content = new String(Files.readAllBytes(filePath));
        assertTrue(content.contains("\"name\": \"Json User\""), "JSON should contain name field");
        assertTrue(content.contains("\"id\":"), "JSON should contain id field");
    }

    @Test
    public void testExportToBinary() throws Exception {
        List<Grade> grades = new ArrayList<>();
        Subject sub = SubjectFactory.createSubject("Math", "Core");
        grades.add(new Grade("STU001", sub, 95.0));

        String filename = "test_binary";
        String path = exporter.exportToBinary(grades, filename);

        Path filePath = Paths.get(path);
        assertTrue(Files.exists(filePath), "Binary file should exist");
        assertTrue(filePath.toString().endsWith(".dat"), "File should end with .dat");

        // Verify size > 0
        assertTrue(Files.size(filePath) > 0, "Binary file should not be empty");
    }

    @Test
    public void testImportStreamingCSV() throws Exception {
        // Create a test CSV
        Path csvPath = Paths.get(IMPORT_DIR, "test_import.csv");
        if (!Files.exists(csvPath.getParent())) {
            Files.createDirectories(csvPath.getParent());
        }

        // Create dummy student
        StudentManager sm = new StudentManager();
        Student s = new RegularStudent("Import User", 22, "import@test.com", "1122334455", "2024-01-01");
        // We need to inject this student into SM so import works
        try {
            sm.addStudent(s);
        } catch (Exception e) {
        }
        String id = s.getStudentId();

        // Use simpler format without extra spaces to avoid trimming issues if any
        String csvContent = id + ",Physics,Core,88.5\n" +
                id + ",Chem,Core,92.0";
        Files.write(csvPath, csvContent.getBytes());

        GradeManager gm = new GradeManager();
        BulkImportService importer = new BulkImportService();

        importer.importGrades("test_import.csv", sm, gm);

        System.out.println("DEBUG: Checking grades for " + id);
        List<Grade> grades = gm.getGradesForStudent(id);

        // Verify
        assertNotNull(grades, "Grade list should not be null");
        assertEquals(2, grades.size(), "Should have imported 2 grades");
        assertEquals(88.5, grades.get(0).getGrade(), 0.1, "First grade should match");
    }
}
