package services;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import models.Student;
import utils.Logger;
import java.io.IOException;

public class BatchReportService {

    private final ReportGenerator reportGenerator;
    private final FileExporter fileExporter;

    public BatchReportService() {
        this.reportGenerator = new ReportGenerator();
        this.fileExporter = new FileExporter();
    }

    public void generateBatchReports(List<Student> students, GradeManager gradeManager, int threadCount) {
        if (students == null || students.isEmpty()) {
            System.out.println("No students to process.");
            return;
        }

        if (threadCount < 2)
            threadCount = 2;
        if (threadCount > 8)
            threadCount = 8;

        System.out.println("\nStarting Batch Process...");
        System.out.println("Processing " + students.size() + " students using " + threadCount + " threads.");

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger completedCount = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        // Estimated sequential time (mock: 50ms per report)
        long estimatedSequentialTime = students.size() * 50;

        for (Student student : students) {
            executor.submit(() -> {
                try {
                    long taskStart = System.currentTimeMillis();
                    // Simulate work (CPU bound)
                    // Thread.sleep(10);

                    String content = reportGenerator.generateDetailedReport(student, gradeManager);
                    String filename = "batch_report_" + student.getStudentId();

                    // Critical section handled by NIO or unique filenames
                    fileExporter.exportToFile(filename + ".txt", content);

                    long taskTime = System.currentTimeMillis() - taskStart;
                    int current = completedCount.incrementAndGet();

                    // Simple progress output (console thread safety relying on println sync)
                    System.out.println(String.format("[Thread-%d] Processed %s (%dms) - Progress: %d/%d",
                            Thread.currentThread().getId(),
                            student.getStudentId(),
                            taskTime,
                            current,
                            students.size()));

                } catch (Exception e) {
                    Logger.logError("Failed to process student: " + student.getStudentId(), e);
                    System.out.println("X Failed: " + student.getStudentId());
                }
            });
        }

        executor.shutdown();
        try {
            // Wait a reasonable amount of time based on task size
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                System.out.println("Batch process timed out and forced shutdown.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        long totalTime = System.currentTimeMillis() - startTime;
        double throughput = (double) students.size() / (totalTime / 1000.0);

        System.out.println("\nBATCH COMPLETE");
        System.out.println("__________________________________________________");
        System.out.println("Total Time: " + totalTime + "ms");
        System.out.println("Est. Sequential Time: " + estimatedSequentialTime + "ms");
        System.out.printf("Throughput: %.2f reports/sec%n", throughput);
        System.out.println("Speedup: " + String.format("%.2fx", (double) estimatedSequentialTime / totalTime));
    }
}
