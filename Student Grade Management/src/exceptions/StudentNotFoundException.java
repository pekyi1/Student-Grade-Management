package exceptions;

public class StudentNotFoundException extends GradeManagementException {
    public StudentNotFoundException(String studentId) {
        super("Student with ID '" + studentId + "' not found in the system.");
    }
}
