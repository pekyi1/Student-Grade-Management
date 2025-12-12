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
    public void testParseInvalidFileFormat(@TempDir Path tempDir) throws IOException, InvalidFileFormatException {
        // Create a temporary file with invalid content
        File tempFile = tempDir.resolve("invalid_grades.csv").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("STU001,Math,Core\n"); // Missing Grade column (invalid)
        }

        SimpleCSVParser parser = new SimpleCSVParser();

        // The parser should NOT throw exception on partial invalid row,
        // it should return the row so BulkImportService can log it.
        List<String[]> records = parser.parse(tempFile.getAbsolutePath());

        assertEquals(1, records.size());
        assertEquals(3, records.get(0).length); // Verify it returns 3 parts
    }
}
