//I have created this class to create some seed data so that 
//I dont have to always add a new student when i update the code
public class DataSeeder {
    public static void seedStudents(StudentManager studentManager) {
        try {
            studentManager.addStudent(new RegularStudent("Alice Johnson", 18, "alice@example.com", "555-0101")); // STU001
            studentManager.addStudent(new HonorsStudent("Bob Smith", 19, "bob@example.com", "555-0102")); // STU002
            studentManager.addStudent(new RegularStudent("Charlie Brown", 18, "charlie@example.com", "555-0103")); // STU003
            studentManager.addStudent(new HonorsStudent("Diana Prince", 19, "diana@example.com", "555-0104")); // STU004
            studentManager.addStudent(new RegularStudent("Evan Wright", 18, "evan@example.com", "555-0105")); // STU005
            studentManager.addStudent(new RegularStudent("Fiona Green", 19, "fiona@example.com", "555-0106")); // STU006
            studentManager.addStudent(new HonorsStudent("George Hill", 18, "george@example.com", "555-0107")); // STU007
            studentManager.addStudent(new RegularStudent("Hannah White", 19, "hannah@example.com", "555-0108")); // STU008
            studentManager.addStudent(new HonorsStudent("Ian Black", 18, "ian@example.com", "555-0109")); // STU009
            studentManager.addStudent(new RegularStudent("Julia Roberts", 19, "julia@example.com", "555-0110")); // STU010
            studentManager.addStudent(new RegularStudent("Kevin Hart", 18, "kevin@example.com", "555-0111")); // STU011
            studentManager.addStudent(new HonorsStudent("Laura Croft", 19, "laura@example.com", "555-0112")); // STU012
            studentManager.addStudent(new RegularStudent("Mike Ross", 18, "mike@example.com", "555-0113")); // STU013
            studentManager.addStudent(new HonorsStudent("Nina Simone", 19, "nina@example.com", "555-0114")); // STU014
            studentManager.addStudent(new RegularStudent("Oscar Wilde", 18, "oscar@example.com", "555-0115")); // STU015
            studentManager.addStudent(new RegularStudent("Paul Rudd", 19, "paul@example.com", "555-0116")); // STU016
            studentManager.addStudent(new HonorsStudent("Quinn Fabray", 18, "quinn@example.com", "555-0117")); // STU017
            studentManager.addStudent(new RegularStudent("Rachel Green", 19, "rachel@example.com", "555-0118")); // STU018
            studentManager.addStudent(new HonorsStudent("Steve Rogers", 18, "steve@example.com", "555-0119")); // STU019
            studentManager.addStudent(new RegularStudent("Tony Stark", 19, "tony@example.com", "555-0120")); // STU020

            System.out.println("✓ Seeded 20 students (STU001 - STU020)");
        } catch (Exception e) {
            System.out.println("Error seeding data: " + e.getMessage());
        }
    }
}
