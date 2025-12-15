package utils;

import exceptions.InvalidDataException;
import java.util.regex.Pattern;

public class ValidationUtils {
    // Regex Patterns
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^STU\\d{3}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_%+-.]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$");
    // Phone supports: (123) 456-7890, 123-456-7890, +1-123-456-7890, 1234567890
    private static final Pattern PHONE_PATTERN = Pattern
            .compile("^(\\+1[- ]?)?(\\(?\\d{3}\\)?[- ]?)\\d{3}[- ]?\\d{4}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s\\-']+$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Z]{3}\\d{3}$");
    // Grade: 0-100
    private static final Pattern GRADE_PATTERN = Pattern.compile("^(100|[1-9]?\\d)$");

    public static void validateStudentId(String id) throws InvalidDataException {
        if (id == null || !STUDENT_ID_PATTERN.matcher(id).matches()) {
            throw new InvalidDataException("Invalid Student ID format. Pattern required: STU### (e.g., STU001)");
        }
    }

    public static void validateEmail(String email) throws InvalidDataException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidDataException("Invalid email format. (Example: user@example.com)");
        }
    }

    public static void validatePhone(String phone) throws InvalidDataException {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidDataException(
                    "Invalid phone format. Accepted: (123) 456-7890, 123-456-7890, +1-123-456-7890, 1234567890");
        }
    }

    public static void validateName(String name) throws InvalidDataException {
        if (name == null || !NAME_PATTERN.matcher(name).matches()) {
            throw new InvalidDataException(
                    "Invalid name format. Only letters, spaces, hyphens, and apostrophes allowed.");
        }
    }

    public static void validateDate(String date) throws InvalidDataException {
        if (date == null || !DATE_PATTERN.matcher(date).matches()) {
            throw new InvalidDataException("Invalid date format. Pattern required: YYYY-MM-DD");
        }
    }

    public static void validateCourseCode(String code) throws InvalidDataException {
        if (code == null || !COURSE_CODE_PATTERN.matcher(code).matches()) {
            throw new InvalidDataException(
                    "Invalid course code. Pattern required: 3 Upper Letters + 3 Digits (e.g., MAT101)");
        }
    }

    public static void validateGrade(double grade) throws InvalidDataException {
        // Checking regex string vs double value, usually for input strings.
        // If double is passed, just range check is safer. But instructions said "Grade
        // Pattern".
        // Let's overload or check. Assuming input is string usually, but system uses
        // double internally.
        if (grade < 0 || grade > 100) {
            throw new InvalidDataException("Grade must be between 0 and 100.");
        }
    }

    // Overload for string input validation before parsing
    public static void validateGradeInput(String gradeStr) throws InvalidDataException {
        if (gradeStr == null || !GRADE_PATTERN.matcher(gradeStr).matches()) {
            throw new InvalidDataException("Invalid grade format. Must be a number 0-100.");
        }
    }

    // Kept for backward compatibility if used, but redirected to range check
    public static void validateAge(int age) throws InvalidDataException {
        if (age < 0 || age > 120) { // relaxed upper bound
            throw new InvalidDataException("Invalid age.");
        }
    }
}
