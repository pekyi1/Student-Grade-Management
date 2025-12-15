package services;

import exceptions.InvalidDataException;
import models.Subject;
import models.CoreSubject;
import models.ElectiveSubject;

public class SubjectFactory {
    public static Subject createSubject(String subjectName, String subjectType) throws InvalidDataException {
        String dummyCode = "IMP" + System.currentTimeMillis();
        return createSubject(subjectName, dummyCode, subjectType);
    }

    public static Subject createSubject(String subjectName, String subjectCode, String subjectType)
            throws InvalidDataException {
        if (subjectType.equalsIgnoreCase("Core")) {
            return new CoreSubject(subjectName, subjectCode);
        } else if (subjectType.equalsIgnoreCase("Elective")) {
            return new ElectiveSubject(subjectName, subjectCode);
        } else {
            throw new InvalidDataException("Invalid subject type: " + subjectType);
        }
    }
}
