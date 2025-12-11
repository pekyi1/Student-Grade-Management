import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import models.*;
import services.GradeManager;
import static org.junit.jupiter.api.Assertions.*;

public class GradeManagerTest {

    private GradeManager gradeManager;

    @BeforeEach
    public void setUp() {
        gradeManager = new GradeManager();
    }

    @Test
    public void testRecordGrade() {
        assertDoesNotThrow(() -> {
            Subject math = new CoreSubject("Math", "MTH101");
            Grade grade = new Grade("STU001", math, 85.0);
            gradeManager.addGrade(grade);
        });

        List<Grade> grades = gradeManager.getGradesForStudent("STU001");
        assertNotNull(grades);
        assertEquals(1, grades.size());
        assertEquals("Math", grades.get(0).getSubject().getSubjectName());
        assertEquals(85.0, grades.get(0).getGrade());
    }

    @Test
    public void testRecordMultipleGrades() {
        assertDoesNotThrow(() -> {
            Subject math = new CoreSubject("Math", "MTH101");
            Subject science = new CoreSubject("Science", "SCI101");
            gradeManager.addGrade(new Grade("STU001", math, 85.0));
            gradeManager.addGrade(new Grade("STU001", science, 90.0));
        });

        List<Grade> grades = gradeManager.getGradesForStudent("STU001");
        assertEquals(2, grades.size());
    }

    @Test
    public void testGetGradesForNonExistentStudent() {
        List<Grade> grades = gradeManager.getGradesForStudent("NONEXISTENT");
        assertNotNull(grades);
        assertTrue(grades.isEmpty());
    }

    @Test
    public void testCalculateClassRank() {
        assertDoesNotThrow(() -> {
            Subject math = new CoreSubject("Math", "MTH101");
            gradeManager.addGrade(new Grade("STU001", math, 90.0)); // Avg 90
            gradeManager.addGrade(new Grade("STU002", math, 80.0)); // Avg 80
            gradeManager.addGrade(new Grade("STU003", math, 95.0)); // Avg 95
        });

        assertEquals(1, gradeManager.calculateClassRank("STU003"));
        assertEquals(2, gradeManager.calculateClassRank("STU001"));
        assertEquals(3, gradeManager.calculateClassRank("STU002"));
    }

    @Test
    public void testClassRankWithNoGrades() {
        assertEquals(-1, gradeManager.calculateClassRank("STU001"));
    }
}
