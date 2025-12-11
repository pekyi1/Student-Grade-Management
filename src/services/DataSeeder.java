//I have created this class to create some seed data so that 
//I dont have to always add a new student when i update the code
package services;

import models.Student;
import models.RegularStudent;
import models.HonorsStudent;
import models.Grade;
import models.Subject;
import models.CoreSubject;
import models.ElectiveSubject;

public class DataSeeder {
    public static void seedStudents(StudentManager studentManager, GradeManager gradeManager) {
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

            // Now add some sample grades for Bob Smith (STU002) if gradeManager is provided
            if (gradeManager != null) {
                // find Bob Smith by name from the student manager
                Student bob = null;
                for (Student s : studentManager.getAllStudents()) {
                    if (s.getName().equalsIgnoreCase("Bob Smith")) {
                        bob = s;
                        break;
                    }
                }

                if (bob != null) {
                    String bobId = bob.getStudentId();
                    try {
                        // Core subjects
                        Subject math = new CoreSubject("Mathematics", "MAT101");
                        Subject english = new CoreSubject("English", "ENG101");
                        Subject science = new CoreSubject("Science", "SCI101");

                        // Electives
                        Subject music = new ElectiveSubject("Music", "MUS101");
                        Subject art = new ElectiveSubject("Art", "ART101");

                        gradeManager.addGrade(new Grade(bobId, math, 88.5));
                        gradeManager.addGrade(new Grade(bobId, english, 76.0));
                        gradeManager.addGrade(new Grade(bobId, science, 69.5));
                        gradeManager.addGrade(new Grade(bobId, music, 92.0));
                        gradeManager.addGrade(new Grade(bobId, art, 81.0));

                        System.out.println("✓ Seeded sample grades for Bob Smith (STU002)");
                    } catch (Exception ex) {
                        System.out.println("Error seeding grades for Bob Smith: " + ex.getMessage());
                    }
                } else {
                    System.out.println("Could not find Bob Smith to seed grades.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error seeding data: " + e.getMessage());
        }
    }
}
