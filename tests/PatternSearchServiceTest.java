
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import services.PatternSearchService;
import models.Student;
import models.RegularStudent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class PatternSearchServiceTest {

    private PatternSearchService service;
    private List<Student> students;

    @BeforeEach
    public void setup() throws Exception {
        service = new PatternSearchService();
        students = new ArrayList<>();
        students.add(new RegularStudent("Alice Johnson", 20, "alice@university.edu", "555-123-4567", "2024-01-01"));
        students.add(new RegularStudent("Bob Smith", 21, "bob@college.org", "555-987-6543", "2024-01-01"));
        students.add(new RegularStudent("Charlie Brown", 22, "charlie@university.edu", "555-555-5555", "2024-01-01"));
    }

    @Test
    public void testEmailDomainSearch() {
        // Regex for ending in @university.edu
        String regex = ".*@university\\.edu$";
        PatternSearchService.SearchResponse response = service.searchByPattern(
                students,
                regex,
                Student::getEmail);

        assertEquals(2, response.results.size());
        assertEquals("Alice Johnson", response.results.get(0).getStudent().getName());
        assertEquals("Charlie Brown", response.results.get(1).getStudent().getName());

        // Verify stats
        assertEquals(3, response.stats.totalScanned);
        assertEquals(2, response.stats.matchesFound);
    }

    @Test
    public void testNamePatternSearch() {
        // Regex for containing "li" (Alice, Charlie)
        String regex = ".*li.*";
        PatternSearchService.SearchResponse response = service.searchByPattern(
                students,
                regex,
                Student::getName);

        assertEquals(2, response.results.size());

        // Check Highlighting contains ANSI code
        String hl = response.results.get(0).getHighlightedText();
        assertTrue(hl.contains("\u001B[43m"), "Highlighted text should contain ANSI background code");
    }

    @Test
    public void testInvalidRegexWithError() {
        String invalidRegex = "[";
        assertThrows(PatternSyntaxException.class, () -> {
            service.searchByPattern(students, invalidRegex, Student::getName);
        });
    }
}
