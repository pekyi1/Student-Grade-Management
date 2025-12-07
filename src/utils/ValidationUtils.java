package utils;

import exceptions.InvalidDataException;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public static void validateEmail(String email) throws InvalidDataException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidDataException("Invalid email format.");
        }
    }

    public static void validateAge(int age) throws InvalidDataException {
        if (age < 0 || age > 60) {
            throw new InvalidDataException("Student age must be between 0 and 60.");
        }
    }

    public static void validatePhone(String phone) throws InvalidDataException {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidDataException("Invalid phone number. Must be 10 digits.");
        }
    }
}
