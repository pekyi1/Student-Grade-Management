import org.junit.jupiter.api.Test;
import utils.ValidationUtils;
import exceptions.InvalidDataException;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {

    @Test
    public void testValidEmail() {
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("test@example.com"));
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("user.name+tag@domain.co.uk"));
    }

    @Test
    public void testInvalidEmail() {
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateEmail("invalid-email"));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateEmail(null));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateEmail(""));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateEmail("user@.com"));
    }

    @Test
    public void testValidAge() {
        assertDoesNotThrow(() -> ValidationUtils.validateAge(0));
        assertDoesNotThrow(() -> ValidationUtils.validateAge(30));
        assertDoesNotThrow(() -> ValidationUtils.validateAge(60));
    }

    @Test
    public void testInvalidAge() {
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateAge(-1));
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validateAge(61));
    }

    @Test
    public void testValidPhone() {
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("1234567890"));
    }

    @Test
    public void testInvalidPhone() {
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validatePhone("123456789")); // 9 digits
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validatePhone("12345678901")); // 11 digits
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validatePhone("abcdefghij")); // Non-numeric
        assertThrows(InvalidDataException.class, () -> ValidationUtils.validatePhone(null));
    }
}
