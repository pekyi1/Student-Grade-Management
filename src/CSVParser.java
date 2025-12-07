import exceptions.InvalidFileFormatException;
import java.io.IOException;
import java.util.List;

public interface CSVParser {
    List<String[]> parse(String filePath) throws IOException, InvalidFileFormatException;
}
