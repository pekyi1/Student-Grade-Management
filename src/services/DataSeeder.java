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
                        // Seed 100 random students
                        String[] firstNames = { "James", "John", "Robert", "Michael", "William", "David", "Richard",
                                        "Joseph",
                                        "Thomas", "Charles", "Christopher", "Daniel", "Matthew", "Anthony", "Donald",
                                        "Mark", "Paul",
                                        "Steven", "Andrew", "Kenneth", "George", "Joshua", "Kevin", "Brian", "Edward",
                                        "Ronald", "Timothy",
                                        "Jason", "Jeffrey", "Ryan", "Jacob", "Gary", "Nicholas", "Eric", "Stephen",
                                        "Jonathan", "Larry",
                                        "Justin", "Scott", "Brandon", "Frank", "Benjamin", "Gregory", "Samuel",
                                        "Raymond", "Patrick",
                                        "Alexander", "Jack", "Dennis", "Jerry", "Tyler", "Aaron", "Henry", "Jose",
                                        "Douglas", "Peter",
                                        "Adam", "Nathan", "Zachary", "Walter", "Kyle", "Harold", "Carl", "Jeremy",
                                        "Keith", "Roger",
                                        "Gerald", "Ethan", "Arthur", "Terry", "Christian", "Sean", "Lawrence", "Austin",
                                        "Joe", "Noah",
                                        "Jesse", "Albert", "Billy", "Bruce", "Willie", "Jordan", "Dylan", "Bryan",
                                        "Eugene", "Madison",
                                        "Abigail", "Olivia", "Emma", "Ava", "Isabella", "Sophia", "Charlotte", "Mia",
                                        "Amelia", "Harper",
                                        "Evelyn" };
                        String[] lastNames = { "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller",
                                        "Wilson",
                                        "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin",
                                        "Thompson",
                                        "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee",
                                        "Walker", "Hall", "Allen",
                                        "Young", "Hernandez", "King", "Wright", "Lopez", "Hill", "Scott", "Green",
                                        "Adams", "Baker",
                                        "Gonzalez", "Nelson", "Carter", "Mitchell", "Perez", "Roberts", "Turner",
                                        "Phillips", "Campbell",
                                        "Parker", "Evans", "Edwards", "Collins" };

                        java.util.Random rand = new java.util.Random();
                        for (int i = 0; i < 100; i++) {
                                String first = firstNames[rand.nextInt(firstNames.length)];
                                String last = lastNames[rand.nextInt(lastNames.length)];
                                String name = first + " " + last;
                                int age = 18 + rand.nextInt(8); // 18-25
                                String email = first.toLowerCase() + "." + last.toLowerCase() + rand.nextInt(1000)
                                                + "@example.com";
                                // Generate 10 digit phone
                                String phone = String.format("555%07d", rand.nextInt(10000000));

                                // Enrollment date random within 2024
                                int month = 1 + rand.nextInt(12);
                                int day = 1 + rand.nextInt(28);
                                String date = String.format("2024-%02d-%02d", month, day);

                                if (rand.nextBoolean()) {
                                        studentManager.addStudent(new RegularStudent(name, age, email, phone, date));
                                } else {
                                        studentManager.addStudent(new HonorsStudent(name, age, email, phone, date));
                                }
                        }
                        System.out.println("✓ Seeded 100 random students");

                        // Add grades for first 25 students
                        java.util.List<Student> allStudents = studentManager.getAllStudents();
                        int limit = Math.min(25, allStudents.size());

                        Subject[] coreSubjects = {
                                        new CoreSubject("Mathematics", "MAT101"),
                                        new CoreSubject("Physics", "PHY101"),
                                        new CoreSubject("Chemistry", "CHE101"),
                                        new CoreSubject("English", "ENG101")
                        };

                        Subject[] electiveSubjects = {
                                        new ElectiveSubject("Art", "ART101"),
                                        new ElectiveSubject("History", "HIS101"),
                                        new ElectiveSubject("Music", "MUS101"),
                                        new ElectiveSubject("Computer Science", "CSC101")
                        };

                        System.out.println("Seeding grades for first " + limit + " students...");

                        for (int i = 0; i < limit; i++) {
                                Student s = allStudents.get(i);

                                // Add grades for all core subjects
                                for (Subject sub : coreSubjects) {
                                        double score = 60 + rand.nextInt(41); // 60-100
                                        gradeManager.addGrade(new Grade(s.getStudentId(), sub, score));
                                }

                                // Add grades for 2 unique electives
                                int idx1 = rand.nextInt(electiveSubjects.length);
                                int idx2 = rand.nextInt(electiveSubjects.length);
                                while (idx1 == idx2) {
                                        idx2 = rand.nextInt(electiveSubjects.length);
                                }

                                gradeManager.addGrade(new Grade(s.getStudentId(), electiveSubjects[idx1],
                                                60 + rand.nextInt(41)));
                                gradeManager.addGrade(new Grade(s.getStudentId(), electiveSubjects[idx2],
                                                60 + rand.nextInt(41)));
                        }
                        System.out.println("✓ Seeded grades for " + limit + " students");

                } catch (Exception e) {
                        System.out.println("Error seeding data: " + e.getMessage());
                }
        }
}
