public abstract class Subject {
    private String subjectName;
    private String subjectCode;

    public Subject(String subjectName, String subjectCode){
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }
    public String getSubjectName(){
        return subjectName;
    }
    public String getSubjectCode(){
        return subjectCode;
    }


    public abstract void displaySubjectDetails();
    public abstract String getSubjectType();
    public abstract boolean isMandatory();
}
