import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import exceptions.InvalidDataException;
import static org.junit.jupiter.api.Assertions.*;

public class ClassStatisticsTest {

    private ClassStatistics classStatistics;

    @BeforeEach
    public void setUp() {
        classStatistics = new ClassStatistics();
    }

    @Test
    public void testGenerateClassStatisticsReport() throws InvalidDataException {
        Subject math = new CoreSubject("Math", "MTH101");

        Grade g1 = new Grade("STU001", math, 90.0);
        Grade g2 = new Grade("STU002", math, 80.0);
        Grade g3 = new Grade("STU003", math, 70.0);
        List<Grade> grades = Arrays.asList(g1, g2, g3);

        // Need Students list for mapping types
        Student s1 = new RegularStudent("Alice", 20, "alice@test.com", "1234567890");
        Student s2 = new RegularStudent("Bob", 20, "bob@test.com", "1234567890");
        Student s3 = new RegularStudent("Charlie", 20, "charlie@test.com", "1234567890");
        // Note: Students are not linked to grades by object ref, but by ID potentially
        // if report uses lookup.
        // Grade objects have studentId. Student objects have studentId
        // (auto-generated).
        // Testing might be tricky if IDs don't match.
        // Grade constructor takes StudentID string.
        // I need to ensure Grade objects are created with IDs from Student objects.

        Grade realG1 = new Grade(s1.getStudentId(), math, 90.0);
        Grade realG2 = new Grade(s2.getStudentId(), math, 80.0);
        Grade realG3 = new Grade(s3.getStudentId(), math, 70.0);
        List<Grade> realGrades = Arrays.asList(realG1, realG2, realG3);
        List<Student> students = Arrays.asList(s1, s2, s3);

        String report = classStatistics.generateClassStatisticsReport(realGrades, students);

        assertNotNull(report);
        assertTrue(report.contains("Total Students: 3"));
        assertTrue(report.contains("Mean (Average):      80.0%"));
        assertTrue(report.contains("Median:              80.0%"));
        // Range: 90-70 = 20.0%
        assertTrue(report.contains("Range:               20.0%"));
        // Highest: 90
        assertTrue(report.contains("Highest Grade:       90%"));
        // Lowest: 70
        assertTrue(report.contains("Lowest Grade:        70%"));
    }

    @Test
    public void testReportWithNoGrades() {
        String report = classStatistics.generateClassStatisticsReport(Collections.emptyList(), Collections.emptyList());
        assertEquals("No grades available for statistics.", report);
    }
}
