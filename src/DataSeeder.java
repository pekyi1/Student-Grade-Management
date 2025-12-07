//I have created this class to create some seed data so that 
//I dont have to always add a new student when i update the code
public class DataSeeder {
    public static void seedStudents(StudentManager studentManager) {
        try {
            studentManager.addStudent(new RegularStudent("Alice Johnson", 18, "alice@example.com", "5555550101")); // STU001
            studentManager.addStudent(new HonorsStudent("Bob Smith", 19, "bob@example.com", "5555550102")); // STU002
            studentManager.addStudent(new RegularStudent("Charlie Brown", 18, "charlie@example.com", "5555550103")); // STU003
            studentManager.addStudent(new HonorsStudent("Diana Prince", 19, "diana@example.com", "5555550104")); // STU004
            studentManager.addStudent(new RegularStudent("Evan Wright", 18, "evan@example.com", "5555550105")); // STU005
            studentManager.addStudent(new RegularStudent("Fiona Green", 19, "fiona@example.com", "5555550106")); // STU006
            studentManager.addStudent(new HonorsStudent("George Hill", 18, "george@example.com", "5555550107")); // STU007
            studentManager.addStudent(new RegularStudent("Hannah White", 19, "hannah@example.com", "5555550108")); // STU008
            studentManager.addStudent(new HonorsStudent("Ian Black", 18, "ian@example.com", "5555550109")); // STU009
            studentManager.addStudent(new RegularStudent("Julia Roberts", 19, "julia@example.com", "5555550110")); // STU010
            studentManager.addStudent(new RegularStudent("Kevin Hart", 18, "kevin@example.com", "5555550111")); // STU011
            studentManager.addStudent(new HonorsStudent("Laura Croft", 19, "laura@example.com", "5555550112")); // STU012
            studentManager.addStudent(new RegularStudent("Mike Ross", 18, "mike@example.com", "5555550113")); // STU013
            studentManager.addStudent(new HonorsStudent("Nina Simone", 19, "nina@example.com", "5555550114")); // STU014
            studentManager.addStudent(new RegularStudent("Oscar Wilde", 18, "oscar@example.com", "5555550115")); // STU015
            studentManager.addStudent(new RegularStudent("Paul Rudd", 19, "paul@example.com", "5555550116")); // STU016
            studentManager.addStudent(new HonorsStudent("Quinn Fabray", 18, "quinn@example.com", "5555550117")); // STU017
            studentManager.addStudent(new RegularStudent("Rachel Green", 19, "rachel@example.com", "5555550118")); // STU018
            studentManager.addStudent(new HonorsStudent("Steve Rogers", 18, "steve@example.com", "5555550119")); // STU019
            studentManager.addStudent(new RegularStudent("Tony Stark", 19, "tony@example.com", "5555550120")); // STU020

            System.out.println("✓ Seeded 20 students (STU001 - STU020)");
        } catch (Exception e) {
            System.out.println("Error seeding data: " + e.getMessage());
        }
    }
}
