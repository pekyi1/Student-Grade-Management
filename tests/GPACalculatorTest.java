import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GPACalculatorTest {

    private GPACalculator gpaCalculator;

    @BeforeEach
    public void setUp() {
        gpaCalculator = new GPACalculator();
    }

    @Test
    public void testConvertToGPA() {
        assertEquals(4.0, gpaCalculator.convertToGPA(93.0));
        assertEquals(3.7, gpaCalculator.convertToGPA(90.0));
        assertEquals(3.3, gpaCalculator.convertToGPA(87.0));
        assertEquals(2.0, gpaCalculator.convertToGPA(73.0));
        assertEquals(1.0, gpaCalculator.convertToGPA(60.0));
        assertEquals(0.0, gpaCalculator.convertToGPA(50.0));
    }

    @Test
    public void testGetLetter() {
        assertEquals("A", gpaCalculator.getLetter(95.0));
        assertEquals("A-", gpaCalculator.getLetter(90.0));
        assertEquals("B+", gpaCalculator.getLetter(87.0));
        assertEquals("F", gpaCalculator.getLetter(50.0));
    }

    @Test
    public void testCalculateCumulativeGPA() {
        Subject math = new CoreSubject("Math", "MTH101");
        Subject science = new CoreSubject("Science", "SCI101");

        Grade g1 = new Grade("STU001", math, 95.0); // 4.0
        Grade g2 = new Grade("STU001", science, 85.0); // 3.0 (83-86 is B = 3.0)

        List<Grade> grades = Arrays.asList(g1, g2);

        assertEquals(3.5, gpaCalculator.calculateCumulativeGPA(grades));
    }

    @Test
    public void testCalculateCumulativeGPAEmpty() {
        assertEquals(0.0, gpaCalculator.calculateCumulativeGPA(Collections.emptyList()));
    }
}
