package models;

public abstract class Subject {
    private String subjectName;
    private String subjectCode;

    public Subject(String subjectName, String subjectCode) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public abstract void displaySubjectDetails();

    public abstract String getSubjectType();

    public abstract boolean isMandatory();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Subject subject = (Subject) o;
        return subjectName.equals(subject.subjectName) && subjectCode.equals(subject.subjectCode);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(subjectName, subjectCode);
    }
}
