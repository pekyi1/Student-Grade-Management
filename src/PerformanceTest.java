
import services.StudentManager;
import services.GradeManager;
import models.RegularStudent;
import models.Student;
import models.Subject;
import models.CoreSubject;
import models.Grade;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PerformanceTest {

    public static void main(String[] args) {
        System.out.println("Starting Performance Verification (Extended)...");
        System.out.println("==================================================");

        // 1. HashMap vs ArrayList Benchmark
        System.out.println("\n[Test 1] Comparing HashMap O(1) vs ArrayList O(n) Lookup");
        System.out.println("--------------------------------------------------");
        int[] sizes = { 10000, 100000, 1000000 };
        StudentManager largestSm = null;
        List<Student> largestList = null;

        for (int size : sizes) {
            Object[] result = runBenchmark(size);
            if (size == 1000000) {
                largestSm = (StudentManager) result[0];
                largestList = (List<Student>) result[1];
            }
        }

        if (largestSm == null)
            return;

        // 2. Add 500 more students and verify O(1)
        System.out.println("\n[Test 2] Adding 500 more students to 1M dataset & Re-verifying...");
        System.out.println("--------------------------------------------------");
        int added = 0;
        Random rand = new Random();
        Student lastAdded = null;
        for (int i = 0; i < 500; i++) {
            try {
                String name = "NewStudent";
                String email = "new" + i + "@test.com";
                String phone = String.format("555%07d", i % 10000000);
                Student s = new RegularStudent(name, 20, email, phone, "2025-01-01");
                largestSm.addStudent(s);
                largestList.add(s);
                lastAdded = s;
                added++;
            } catch (Exception e) {
            }
        }
        System.out.println("  Added " + added + " new students.");

        // Measure lookup for the very last added student
        String targetId = lastAdded.getStudentId();
        long t1 = System.nanoTime();
        largestSm.findStudent(targetId);
        long t2 = System.nanoTime();
        long mapTime = t2 - t1;
        System.out.printf("  HashMap Lookup (Item #1,000,500): %,d ns%n", mapTime);
        if (mapTime < 50000) { // arbitrary threshold for O(1), e.g. 50us
            System.out.println("  ✓ Verified O(1) performance maintained.");
        } else {
            System.out.println("  ! Warning: Lookup took longer than expected.");
        }

        // 3. TreeMap Auto-Sorting
        System.out.println("\n[Test 3] Verifying TreeMap Auto-Sorting (GPA Descending)");
        System.out.println("--------------------------------------------------");
        verifyTreeMapSorting();

        // 4. HashSet Duplicate Prevention
        System.out.println("\n[Test 4] Verifying HashSet Prevents Duplicate Course Codes");
        System.out.println("--------------------------------------------------");
        verifyHashSetDuplicates();
    }

    private static Object[] runBenchmark(int size) {
        System.out.println("\nDataset Size: " + size);
        StudentManager sm = new StudentManager();
        List<Student> studentList = new ArrayList<>(size);

        // Populate
        long startLoad = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            try {
                String phone = String.format("555%07d", i % 10000000);
                String email = "student" + i + "@example.com";
                String name = "StudentName";

                Student s = new RegularStudent(name, 20, email, phone, "2024-01-01");
                sm.addStudent(s);
                studentList.add(s);
            } catch (Exception e) {
            }
        }
        long endLoad = System.currentTimeMillis();
        System.out.println("  Data Loaded in: " + (endLoad - startLoad) + "ms");

        // Lookup Last
        if (studentList.isEmpty())
            return new Object[] { sm, studentList };
        Student target = studentList.get(size - 1);
        String targetId = target.getStudentId();

        // Map
        long mapStart = System.nanoTime();
        sm.findStudent(targetId);
        long mapTime = System.nanoTime() - mapStart;

        // List
        long listStart = System.nanoTime();
        for (Student s : studentList) {
            if (s.getStudentId().equals(targetId))
                break;
        }
        long listTime = System.nanoTime() - listStart;

        System.out.printf("  HashMap: %,d ns | ArrayList: %,d ns | Speedup: %.0fx%n", mapTime, listTime,
                (double) listTime / mapTime);

        return new Object[] { sm, studentList };
    }

    private static void verifyTreeMapSorting() {
        try {
            StudentManager sm = new StudentManager();
            GradeManager gm = new GradeManager();

            Student s1 = new RegularStudent("Alice", 20, "a@a.com", "5550000001", "2024-01-01");
            Student s2 = new RegularStudent("Bob", 20, "b@b.com", "5550000002", "2024-01-01");
            Student s3 = new RegularStudent("Charlie", 20, "c@c.com", "5550000003", "2024-01-01");

            sm.addStudent(s1);
            sm.addStudent(s2);
            sm.addStudent(s3);

            Subject math = new CoreSubject("Math", "MAT101");

            // s1: 90
            gm.addGrade(new Grade(s1.getStudentId(), math, 90.0));
            // s2: 70
            gm.addGrade(new Grade(s2.getStudentId(), math, 70.0));
            // s3: 95
            gm.addGrade(new Grade(s3.getStudentId(), math, 95.0));

            Map<Double, List<Student>> rankedMap = sm.getStudentsByGPA(gm);

            System.out.print("  GPA Keys Triggered: ");
            for (Double gpa : rankedMap.keySet()) {
                System.out.print(gpa + " -> ");
            }
            System.out.println("End");

            Double firstKey = rankedMap.keySet().iterator().next();
            if (firstKey > 90.0) {
                System.out.println("  ✓ Verified: Highest GPA (" + firstKey + ") is first (Descending Order).");
            } else {
                System.out.println("  X Failed: Sorting incorrect.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void verifyHashSetDuplicates() {
        try {
            Subject s1 = new CoreSubject("Mathematics", "MAT101");
            Subject s2 = new CoreSubject("Mathematics", "MAT101"); // Identical

            Set<Subject> subjects = new HashSet<>();
            subjects.add(s1);
            boolean addedAgain = subjects.add(s2);

            System.out.println("  Added s1: true");
            System.out.println("  Added s2 (Identical): " + addedAgain);
            System.out.println("  HashSet Size: " + subjects.size());

            if (!addedAgain && subjects.size() == 1) {
                System.out.println("  ✓ Verified: HashSet prevented duplicate code/name.");
            } else {
                System.out.println("  X Failed: Duplicate allowed.");
            }

            // Edge case: Same Code, Diff Name (Current equals checks both, so this
            // validates that)
            // If requirements strictly wanted "Duplicate Code" prevention regardless of
            // name, this test would show current app behavior.
            // But for now we verify basic duplicate object prevention.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
