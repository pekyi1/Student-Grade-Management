package services;

import interfaces.Exportable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileExporter {
    private static final String REPORTS_DIR = "reports";

    public String exportToFile(String filename, String content) throws IOException {
        File directory = new File(REPORTS_DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }

        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }

        File file = new File(directory, filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }

        return file.getAbsolutePath();
    }

    public String export(Exportable item, String filename) throws IOException {
        return exportToFile(filename, item.toExportFormat());
    }

    public String exportList(java.util.List<? extends Exportable> items, String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Exportable item : items) {
            sb.append(item.toExportFormat()).append("\n");
        }
        return exportToFile(filename, sb.toString());
    }
}
