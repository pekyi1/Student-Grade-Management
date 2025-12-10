package services;

import exceptions.InvalidFileFormatException;
import interfaces.CSVParser;
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
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                records.add(parts);
            }
        }
        return records;
    }
}
