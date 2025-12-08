import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import exceptions.InvalidDataException;
import static org.junit.jupiter.api.Assertions.*;

public class BulkImportServiceTest {

    private BulkImportService bulkImportService;
    private GradeManager gradeManager;
    private StudentManager studentManager;

    @BeforeEach
    public void setUp() {
        gradeManager = new GradeManager();
        studentManager = new StudentManager();
        try {
            studentManager.addStudent(new RegularStudent("Test Student", 20, "test@test.com", "1234567890"));
        } catch (Exception e) {
            fail("Setup failed: " + e.getMessage());
        }
    }

    @Test
    public void testImportGradesValid() throws IOException {
        // Create a temporary CSV file
        File importDir = new File("imports");
        if (!importDir.exists()) {
            importDir.mkdir();
        }

        // Get the actual ID of the student created in setUp
        Student[] students = studentManager.getAllStudents();
        String studentId = students[0].getStudentId();

        String csvContent = "StudentID, SubjectName, Subject Type, Grade\n" +
                studentId + ", Math, Core, 95\n" +
                studentId + ", English, Core, 88";

        File csvFile = new File("imports/test_valid.csv");
        Files.write(csvFile.toPath(), csvContent.getBytes());

        // Use default constructor
        bulkImportService = new BulkImportService();

        // importGrades takes filename, studentManager, gradeManager
        bulkImportService.importGrades("test_valid.csv", studentManager, gradeManager);

        List<Grade> grades = gradeManager.getGradesForStudent(studentId);
        assertEquals(2, grades.size());

        // Cleanup
        csvFile.delete();
    }

    @Test
    public void testImportGradesWithInvalidRows() throws IOException {
        File importDir = new File("imports");
        if (!importDir.exists()) {
            importDir.mkdir();
        }

        Student[] students = studentManager.getAllStudents();
        String studentId = students[0].getStudentId();

        String csvContent = "StudentID, SubjectName, Subject Type, Grade\n" +
                studentId + ", Math, Core, 95\n" +
                "STU999, Physics, Core, 88\n" + // Invalid Student
                studentId + ", Art, Elective, 105"; // Invalid Grade

        File csvFile = new File("imports/test_invalid.csv");
        Files.write(csvFile.toPath(), csvContent.getBytes());

        bulkImportService = new BulkImportService();
        bulkImportService.importGrades("test_invalid.csv", studentManager, gradeManager);

        List<Grade> grades = gradeManager.getGradesForStudent(studentId);
        assertEquals(1, grades.size(), "Should only import valid grade");

        // Cleanup
        csvFile.delete();
    }
}
