
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import utils.ValidationUtils;
import exceptions.InvalidDataException;

public class ValidationUtilsTest {

    @Test
    public void testValidateStudentId() {
        assertDoesNotThrow(() -> ValidationUtils.validateStudentId("STU001"));
        assertDoesNotThrow(() -> ValidationUtils.validateStudentId("STU999"));

        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateStudentId("stu001")); // Case sensitive
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateStudentId("123"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateStudentId("STU12"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateStudentId(null));
    }

    @Test
    public void testValidateEmail() {
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("test@example.com"));
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("john.doe@univ.edu"));

        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateEmail("invalid-email"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateEmail("@domain.com"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateEmail("user@.com"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateEmail(null));
    }

    @Test
    public void testValidatePhone() {
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("(123) 456-7890"));
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("123-456-7890"));
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("+1-123-456-7890"));
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("1234567890"));

        assertThrows(InvalidDataException.class, () -> ValidationUtils.validatePhone("555-0123")); // Too short
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validatePhone("123-abc-7890")); // Letters
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validatePhone(null));
    }

    @Test
    public void testValidateName() {
        assertDoesNotThrow(() -> ValidationUtils.validateName("John Smith"));
        assertDoesNotThrow(() -> ValidationUtils.validateName("Mary-Jane"));
        assertDoesNotThrow(() -> ValidationUtils.validateName("O'Connor"));

        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateName("John123")); // Digits
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateName("John_Doe")); // Underscore not
                                                                                                  // allowed per spec?
                                                                                                  // Spec says
                                                                                                  // "hyphens". Usually
                                                                                                  // default regex
                                                                                                  // allows underscore
                                                                                                  // if \w used, but
                                                                                                  // mine was specific.
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateName(null));
    }

    @Test
    public void testValidateDate() {
        assertDoesNotThrow(() -> ValidationUtils.validateDate("2024-11-03"));

        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateDate("2024/11/03"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateDate("03-11-2024"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateDate(null));
    }

    @Test
    public void testValidateCourseCode() {
        assertDoesNotThrow(() -> ValidationUtils.validateCourseCode("MAT101"));
        assertDoesNotThrow(() -> ValidationUtils.validateCourseCode("PHY202"));

        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateCourseCode("mat101"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateCourseCode("MAT1"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateCourseCode("101MAT"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateCourseCode(null));
    }

    @Test
    public void testValidateGrade() {
        assertDoesNotThrow(() -> ValidationUtils.validateGrade(100));
        assertDoesNotThrow(() -> ValidationUtils.validateGrade(0));
        assertDoesNotThrow(() -> ValidationUtils.validateGrade(50.5)); // Double check

        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateGrade(-1));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateGrade(101));
    }

    @Test
    public void testValidateGradeInput() {
        assertDoesNotThrow(() -> ValidationUtils.validateGradeInput("100"));
        assertDoesNotThrow(() -> ValidationUtils.validateGradeInput("0"));
        assertDoesNotThrow(() -> ValidationUtils.validateGradeInput("50"));

        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateGradeInput("101"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateGradeInput("-1"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateGradeInput("abc"));
    }
}
