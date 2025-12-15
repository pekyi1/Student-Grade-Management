import models.Grade;
import models.Student;
import models.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.GradeManager;
import services.StreamDataService;
import services.SubjectFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StreamDataServiceTest {

    private StreamDataService streamService;
    private GradeManager gradeManager;
    private List<Student> students;
    private List<Grade> grades;

    // Stub class
    static class TestStudent extends Student {
        private String id;

        public TestStudent(String id, String name, int age, String email) throws Exception {
            super(name, age, email, "555-0000", "2023-01-01", "Active");
            // Reflection or hack to set ID if super doesn't allow setting it easily to
            // specific value
            // But super auto-generates IDs.
            // We can just rely on auto-generation or use a setter if we added one.
            // But to make test deterministic, let's just use the values we get.
            // OR, we can hack the ID field via reflection?
            // Actually, for these tests, we don't strict check ID except for map keys.
        }

        @Override
        public void displayStudentDetails(double currentAverage, int enrolledSubjects) {
        }

        @Override
        public String getStudentType() {
            return "Test";
        }

        @Override
        public double getPassingGrade() {
            return 50.0;
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        streamService = new StreamDataService();
        gradeManager = new GradeManager();
        students = new ArrayList<>();
        grades = new ArrayList<>();

        // Create students
        // We rely on auto-generated IDs being unique.
        Student s1 = new TestStudent("ignore", "Alice", 20, "alice@test.com");
        Student s2 = new TestStudent("ignore", "Bob", 22, "bob@test.com");
        Student s3 = new TestStudent("ignore", "Charlie", 21, "charlie@test.com");

        students.addAll(Arrays.asList(s1, s2, s3));

        // Mock grades
        // We use s1.getStudentId() to ensure linkage
        Grade g1 = new Grade(s1.getStudentId(), SubjectFactory.createSubject("Math", "Core"), 95.0);
        Grade g2 = new Grade(s1.getStudentId(), SubjectFactory.createSubject("English", "Core"), 85.0);
        Grade g3 = new Grade(s2.getStudentId(), SubjectFactory.createSubject("Math", "Core"), 75.0);

        gradeManager.addGrade(g1);
        gradeManager.addGrade(g2);
        gradeManager.addGrade(g3);

        grades.addAll(Arrays.asList(g1, g2, g3));
    }

    @Test
    public void testFilterStudents() {
        List<Student> results = streamService.filterStudents(students, s -> s.getAge() > 20);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(s -> s.getName().equals("Bob")));
    }

    @Test
    public void testExtractEmails() {
        List<String> emails = streamService.extractEmails(students);
        assertEquals(3, emails.size());
        assertTrue(emails.contains("alice@test.com"));
    }

    @Test
    public void testCalculateAverageGradePerSubject() {
        Map<String, Double> averages = streamService.calculateAverageGradePerSubject(grades);
        assertTrue(averages.containsKey("Math"));
        assertEquals(85.0, averages.get("Math"), 0.01);
    }

    @Test
    public void testCountLinesWithKeyword() throws IOException {
        Path tempFile = Files.createTempFile("stream_test", ".txt");
        List<String> lines = Arrays.asList("apple", "banana", "apple pie");
        Files.write(tempFile, lines);
        long count = streamService.countLinesWithKeyword(tempFile, "apple");
        assertEquals(2, count);
        Files.delete(tempFile);
    }
}
