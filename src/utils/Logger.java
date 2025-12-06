package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logError(String message, Exception e) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.err.println("[" + timestamp + "] ERROR: " + message);
        if (e != null) {
            System.err.println("[" + timestamp + "] DETAILS: " + e.getMessage());
        }
    }

    public static void logInfo(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] INFO: " + message);
    }
}
