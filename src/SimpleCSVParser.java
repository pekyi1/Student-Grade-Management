import exceptions.InvalidFileFormatException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleCSVParser implements CSVParser {

    @Override
    public List<String[]> parse(String filePath) throws IOException, InvalidFileFormatException {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header
            br.readLine();
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    throw new InvalidFileFormatException("Invalid format at line " + lineNumber
                            + ": Expected 4 columns, found " + parts.length + ". Row content: " + line);
                }
                records.add(parts);
            }
        }
        return records;
    }
}
