package services;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service to watch for new files in the import directory and trigger import.
 * Runs in a background thread.
 */
public class DirectoryWatcherService implements Runnable {

    private final String importDir;
    private final BulkImportService importService;
    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public DirectoryWatcherService(String importDir, BulkImportService importService,
            StudentManager studentManager, GradeManager gradeManager) {
        this.importDir = importDir;
        this.importService = importService;
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {
        running.set(true);
        Path path = Paths.get(importDir);

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                System.out.println("Directory Watcher started on: " + path.toAbsolutePath());

                while (running.get()) {
                    WatchKey key;
                    try {
                        // Poll with timeout to allow shutdown check
                        key = watchService.poll(1, java.util.concurrent.TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }

                    if (key == null)
                        continue;

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            @SuppressWarnings("unchecked")
                            WatchEvent<Path> ev = (WatchEvent<Path>) event;
                            Path fileName = ev.context();

                            System.out.println("New file detected: " + fileName);
                            // Process file
                            // Add slight delay to ensure file write is complete by OS
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ignored) {
                            }

                            importService.importGrades(fileName.toString(), studentManager, gradeManager);

                            // Move to processed directory to prevent loops
                            try {
                                Path source = path.resolve(fileName);
                                Path processedDir = path.resolve("processed");
                                if (!Files.exists(processedDir)) {
                                    Files.createDirectories(processedDir);
                                }
                                Path target = processedDir.resolve(fileName);
                                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("File moved to: " + target);
                            } catch (IOException e) {
                                System.err.println("Failed to move processed file: " + e.getMessage());
                            }
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error in DirectoryWatcher: " + e.getMessage());
        }
        System.out.println("Directory Watcher stopped.");
    }
}
