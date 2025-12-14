package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single audit log entry.
 * Immutable and thread-safe.
 */
public class AuditEntry {
    private final LocalDateTime timestamp;
    private final long threadId;
    private final String operation;
    private final String details;
    private final String user;
    private final boolean success;
    private final long executionTimeMs;

    public AuditEntry(String operation, String details, String user, boolean success, long executionTimeMs) {
        this.timestamp = LocalDateTime.now();
        this.threadId = Thread.currentThread().getId();
        this.operation = operation;
        this.details = details;
        this.user = user;
        this.success = success;
        this.executionTimeMs = executionTimeMs;
    }

    @Override
    public String toString() {
        return String.format("[%s] [Thread-%d] [%s] %s - %s (User: %s, Time: %dms) - %s",
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                threadId,
                success ? "INFO" : "ERROR",
                operation,
                details,
                user,
                executionTimeMs,
                success ? "SUCCESS" : "FAILURE");
    }

    public String toCSV() {
        // Simple CSV format for file writing
        return String.format("%s,%d,%s,%s,%s,%s,%d",
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                threadId,
                operation,
                escapeCSV(details),
                user,
                success ? "SUCCESS" : "FAILURE",
                executionTimeMs);
    }

    private String escapeCSV(String data) {
        if (data == null)
            return "";
        return "\"" + data.replace("\"", "\"\"") + "\"";
    }

    // Getters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getOperation() {
        return operation;
    }

    public String getDetails() {
        return details;
    }

    public boolean isSuccess() {
        return success;
    }
}
