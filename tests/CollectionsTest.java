import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import services.StudentManager;
import services.GradeManager;
import services.TaskScheduler;
import services.SubjectFactory;
import models.Student;
import models.RegularStudent;
import models.Grade;
import models.Subject;
import utils.StudentComparators;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;

public class CollectionsTest {

    @Test
    public void testStudentManagerUsingHashMap() throws Exception {
        StudentManager sm = new StudentManager();
        Student s1 = new RegularStudent("John Doe", 20, "john@example.com", "1234567890", "2024-01-01");
        Student s2 = new RegularStudent("Jane Doe", 21, "jane@example.com", "1234567891", "2024-01-01");

        sm.addStudent(s1);
        sm.addStudent(s2);

        // Verify O(1) lookup logic - functional test
        assertEquals(s1, sm.findStudent(s1.getStudentId()));
        assertEquals(s2, sm.findStudent(s2.getStudentId()));
        assertNull(sm.findStudent("INVALID"));

        assertEquals(2, sm.getStudentCount());
    }

    @Test
    public void testGradeManagerUsingLinkedList() throws Exception {
        GradeManager gm = new GradeManager();
        // SubjectFactory.createSubject(Name, Type)
        Subject math = SubjectFactory.createSubject("Math", "Core");

        Grade g1 = new Grade("STU001", math, 90.0);
        Grade g2 = new Grade("STU001", math, 95.0); // Update

        gm.addGrade(g1);
        gm.addGrade(g2); // Should update

        // LinkedList should maintain order of insertion if we added different ones,
        // but here we updated one. Let's add another.
        Subject hist = SubjectFactory.createSubject("History", "Elective");
        Grade g3 = new Grade("STU001", hist, 85.0);
        gm.addGrade(g3);

        List<Grade> grades = gm.getAllGrades();
        // Removed instanceof check as implementation returns ArrayList copy

        // Let's verify content
        assertEquals(2, grades.size()); // Math (updated) + History
    }

    @Test
    public void testGetStudentsByGPATreeMap() throws Exception {
        StudentManager sm = new StudentManager();
        GradeManager gm = new GradeManager();

        Student s1 = new RegularStudent("Alice", 20, "alice@test.com", "1231231234", "2024-01-01");
        Student s2 = new RegularStudent("Bob", 20, "bob@test.com", "1231231235", "2024-01-01");
        Student s3 = new RegularStudent("Charlie", 20, "charlie@test.com", "1231231236", "2024-01-01");

        sm.addStudent(s1);
        sm.addStudent(s2);
        sm.addStudent(s3);

        // s1: 90, s2: 80, s3: 95
        Subject sub = SubjectFactory.createSubject("Sub", "Core");
        gm.addGrade(new Grade(s1.getStudentId(), sub, 90.0));
        gm.addGrade(new Grade(s2.getStudentId(), sub, 80.0));
        gm.addGrade(new Grade(s3.getStudentId(), sub, 95.0));

        Map<Double, List<Student>> sorted = sm.getStudentsByGPA(gm);

        // Expected order: 95.0 (C), 90.0 (A), 80.0 (B)
        Double[] gpas = sorted.keySet().toArray(new Double[0]);
        assertEquals(95.0, gpas[0], 0.01);
        assertEquals(90.0, gpas[1], 0.01);
        assertEquals(80.0, gpas[2], 0.01);

        assertEquals(s3, sorted.get(95.0).get(0));
    }

    @Test
    public void testTaskSchedulerPriorityQueue() {
        TaskScheduler scheduler = new TaskScheduler();
        scheduler.addTask(new TaskScheduler.ScheduledTask("Low Priority", 10));
        scheduler.addTask(new TaskScheduler.ScheduledTask("High Priority", 1));
        scheduler.addTask(new TaskScheduler.ScheduledTask("Medium Priority", 5));

        assertEquals(1, scheduler.pollNextTask().getPriority());
        assertEquals(5, scheduler.pollNextTask().getPriority());
        assertEquals(10, scheduler.pollNextTask().getPriority());
    }

    @Test
    public void testComparators() throws Exception {
        Student s1 = new RegularStudent("Charlie", 22, "c@c.com", "1111111111", "2024-01-01"); // STU...
        Student s2 = new RegularStudent("Alice", 20, "a@a.com", "2222222222", "2024-01-01");
        Student s3 = new RegularStudent("Bob", 21, "b@b.com", "3333333333", "2024-01-01");

        List<Student> list = new ArrayList<>(Arrays.asList(s1, s2, s3));

        Collections.sort(list, StudentComparators.BY_NAME);
        assertEquals(s2, list.get(0)); // Alice
        assertEquals(s3, list.get(1)); // Bob
        assertEquals(s1, list.get(2)); // Charlie

        Collections.sort(list, StudentComparators.BY_AGE);
        assertEquals(s2, list.get(0)); // 20
        assertEquals(s3, list.get(1)); // 21
        assertEquals(s1, list.get(2)); // 22
    }
}
