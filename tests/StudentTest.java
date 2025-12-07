import org.junit.jupiter.api.Test;
import exceptions.InvalidDataException;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {

    @Test
    public void testRegularStudentCreation() {
        assertDoesNotThrow(() -> {
            RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "1234567890");
            assertEquals("Regular", student.getStudentType());
            assertEquals(50.0, student.getPassingGrade());
            assertEquals("Active", student.getStatus());
        });
    }

    @Test
    public void testHonorsStudentCreation() {
        assertDoesNotThrow(() -> {
            HonorsStudent student = new HonorsStudent("Bob", 22, "bob@example.com", "0987654321");
            assertEquals("Honors", student.getStudentType());
            assertEquals(60.0, student.getPassingGrade());
        });
    }

    @Test
    public void testHonorsEligibility() {
        assertDoesNotThrow(() -> {
            HonorsStudent student = new HonorsStudent("Charlie", 21, "charlie@example.com", "1122334455");
            assertFalse(student.checkHonorsEligibility(80.0));
            assertTrue(student.checkHonorsEligibility(85.0));
            assertTrue(student.checkHonorsEligibility(90.0));
        });
    }

    @Test
    public void testStudentValidationFailure() {
        // Name validation
        assertThrows(InvalidDataException.class, () -> new RegularStudent(null, 20, "valid@email.com", "1234567890"));
        assertThrows(InvalidDataException.class, () -> new RegularStudent("", 20, "valid@email.com", "1234567890"));

        // Age validation
        assertThrows(InvalidDataException.class,
                () -> new RegularStudent("David", -1, "valid@email.com", "1234567890"));

        // Email validation
        assertThrows(InvalidDataException.class, () -> new RegularStudent("David", 20, "invalid", "1234567890"));

        // Phone validation
        assertThrows(InvalidDataException.class, () -> new RegularStudent("David", 20, "valid@email.com", "123"));
    }
}
