public class ElectiveSubject extends Subject {
   private final boolean mandatory = false;

   public ElectiveSubject(String subjectName, String subjectCode){
       super(subjectName, subjectCode);
   }

   @Override
    public void displaySubjectDetails(){
       System.out.println("-----Subject Details-----");
       System.out.println("Subject Name: " + getSubjectName());
       System.out.println("Subject Code: " + getSubjectCode());
       System.out.println("Type: " + getSubjectType());
       System.out.println("Mandatory: " + (isMandatory() ? "Yes" : "No"));
   }

   @Override
    public String getSubjectType(){
       return "Elective";
   }
   public boolean isMandatory(){
       return mandatory;
   }
}
