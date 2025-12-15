package services;

import models.AuditEntry;
import utils.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AuditLogService {
    private static final String LOG_DIR = "logs/audit";
    private static final String LOG_FILE_PREFIX = "audit_log";
    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    private final Queue<AuditEntry> buffer;
    private final ScheduledExecutorService scheduler;
    private Path currentLogFile;

    public AuditLogService() {
        this.buffer = new ConcurrentLinkedQueue<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        initializeLogDirectory();

        // Schedule periodic flushing to disk every 5 seconds
        this.scheduler.scheduleAtFixedRate(this::flushBuffer, 5, 5, TimeUnit.SECONDS);
    }

    private void initializeLogDirectory() {
        try {
            Path dir = Paths.get(LOG_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            // Initialize current log file
            rotateLogFileIfNeeded();
        } catch (IOException e) {
            Logger.logError("Failed to initialize audit log directory", e);
        }
    }

    /**
     * Logs an operation to the audit trail.
     * This method is non-blocking and thread-safe.
     */
    public void log(String operation, String details, String user, boolean success) {
        log(operation, details, user, success, 0);
    }

    public void log(String operation, String details, String user, boolean success, long executionTimeMs) {
        AuditEntry entry = new AuditEntry(operation, details, user, success, executionTimeMs);
        buffer.offer(entry);
    }

    /**
     * Flushes the buffer to the log file.
     * Called periodically by the scheduler.
     */
    public void flushBuffer() {
        if (buffer.isEmpty())
            return;

        List<AuditEntry> entriesToWrite = new ArrayList<>();
        AuditEntry entry;
        while ((entry = buffer.poll()) != null) {
            entriesToWrite.add(entry);
        }

        if (entriesToWrite.isEmpty())
            return;

        try {
            rotateLogFileIfNeeded();

            try (BufferedWriter writer = Files.newBufferedWriter(currentLogFile,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                for (AuditEntry e : entriesToWrite) {
                    writer.write(e.toCSV());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            // If logging fails, we might print to stderr as a fallback
            System.err.println("CRITICAL: Failed to write to audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void rotateLogFileIfNeeded() throws IOException {
        // If current file isn't set, find or create one
        if (currentLogFile == null || Files.notExists(currentLogFile)) {
            currentLogFile = Paths.get(LOG_DIR, LOG_FILE_PREFIX + ".csv");
            return;
        }

        // Check size
        if (Files.size(currentLogFile) > MAX_FILE_SIZE_BYTES) {
            // Rotate
            String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path rotated = Paths.get(LOG_DIR, LOG_FILE_PREFIX + "_" + timestamp + ".csv");
            Files.move(currentLogFile, rotated);
            // New file will be created by next write
            currentLogFile = Paths.get(LOG_DIR, LOG_FILE_PREFIX + ".csv");
        }
    }

    public List<String> getRecentLogs(int limit) {
        // Read lines from file - simplified.
        // In production, might need to read from end of file backward.
        // For now, let's just read the current file.
        try {
            if (currentLogFile == null || Files.notExists(currentLogFile))
                return new ArrayList<>();
            List<String> lines = Files.readAllLines(currentLogFile);
            int start = Math.max(0, lines.size() - limit);
            return lines.subList(start, lines.size());
        } catch (IOException e) {
            Logger.logError("Failed to read audit logs", e);
            return new ArrayList<>();
        }
    }

    /**
     * Searches logs in the current log file by simplified criteria.
     * Real-world would scan multiple files.
     */
    public List<String> searchLogs(String keyword) {
        try {
            if (currentLogFile == null || Files.notExists(currentLogFile))
                return new ArrayList<>();
            return Files.lines(currentLogFile)
                    .filter(line -> line.contains(keyword))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            Logger.logError("Failed to search logs", e);
            return new ArrayList<>();
        }
    }

    public void shutdown() {
        flushBuffer(); // One last flush
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
