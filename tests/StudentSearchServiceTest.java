import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import models.*;
import services.StudentSearchService;
import services.StudentManager;
import exceptions.InvalidDataException;
import static org.junit.jupiter.api.Assertions.*;

public class StudentSearchServiceTest {

    private StudentSearchService searchService;
    private StudentManager studentManager;

    @BeforeEach
    public void setUp() {
        studentManager = new StudentManager();
        searchService = new StudentSearchService(); // Assuming default constructor
    }

    @Test
    public void testSearchByName() throws InvalidDataException {
        Student s1 = new RegularStudent("Alice Smith", 20, "alice@test.com", "1234567890");
        Student s2 = new RegularStudent("Bob Jones", 20, "bob@test.com", "1234567890");

        // Method expects array, so convert list to array or just create array
        Student[] allStudents = new Student[] { s1, s2 };

        List<Student> results = searchService.findStudentsByName(allStudents, "Smith");
        assertEquals(1, results.size());
        assertEquals("Alice Smith", results.get(0).getName());

        List<Student> results2 = searchService.findStudentsByName(allStudents, "bob"); // Case insensitive check
        assertEquals(1, results2.size());
        assertEquals("Bob Jones", results2.get(0).getName());
    }

    @Test
    public void testSearchByType() throws InvalidDataException {
        Student s1 = new RegularStudent("Alice", 20, "alice@test.com", "1234567890");
        Student s2 = new HonorsStudent("Bob", 20, "bob@test.com", "1234567890");

        Student[] allStudents = new Student[] { s1, s2 };

        List<Student> regularResult = searchService.findStudentsByType(allStudents, "Regular");
        assertEquals(1, regularResult.size());
        assertEquals(s1, regularResult.get(0));

        List<Student> honorsResult = searchService.findStudentsByType(allStudents, "Honors");
        assertEquals(1, honorsResult.size());
        assertEquals(s2, honorsResult.get(0));
    }
}
