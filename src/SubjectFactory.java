import exceptions.InvalidDataException;

public class SubjectFactory {
    public static Subject createSubject(String subjectName, String subjectType) throws InvalidDataException {
        String dummyCode = "IMP" + System.currentTimeMillis();
        if (subjectType.equalsIgnoreCase("Core")) {
            return new CoreSubject(subjectName, dummyCode);
        } else if (subjectType.equalsIgnoreCase("Elective")) {
            return new ElectiveSubject(subjectName, dummyCode);
        } else {
            throw new InvalidDataException("Invalid subject type: " + subjectType);
        }
    }
}
