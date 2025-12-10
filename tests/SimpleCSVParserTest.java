import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import models.*;
import services.SimpleCSVParser;
import exceptions.InvalidFileFormatException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleCSVParserTest {

    @Test
    public void testParseInvalidFileFormat(@TempDir Path tempDir) throws IOException {
        // Create a temporary file with invalid content
        File tempFile = tempDir.resolve("invalid_grades.csv").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("StudentID,Subject,Type,Grade\n"); // Header
            writer.write("STU001,Math,Core\n"); // Missing Grade column (invalid)
        }

        SimpleCSVParser parser = new SimpleCSVParser();

        // Assert that InvalidFileFormatException is thrown
        Exception exception = assertThrows(InvalidFileFormatException.class, () -> {
            parser.parse(tempFile.getAbsolutePath());
        });

        assertTrue(exception.getMessage().contains("Expected 4 columns"));
    }
}
