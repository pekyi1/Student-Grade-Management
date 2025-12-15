package services;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import models.Student;
import models.ClassStatistics;
import utils.Logger;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BatchReportService {

    private final ReportGenerator reportGenerator;
    private final FileExporter fileExporter;

    public BatchReportService() {
        this.reportGenerator = new ReportGenerator();
        this.fileExporter = new FileExporter();
    }

    public enum ReportScope {
        ALL, TYPE, GRADE_RANGE, CUSTOM
    }

    public enum ReportFormat {
        PDF, TEXT, EXCEL, ALL
    }

    public static class BatchConfig {
        public ReportScope scope;
        public ReportFormat format;
        public int threadCount;
        public String filterValue; // e.g. "Regular", "90", "1,2,3"

        public BatchConfig(ReportScope scope, ReportFormat format, int threadCount, String filterValue) {
            this.scope = scope;
            this.format = format;
            this.threadCount = threadCount;
            this.filterValue = filterValue;
        }
    }

    // Thread status tracker
    private Map<Long, String> threadStatus = new ConcurrentHashMap<>();

    public void executeBatch(List<Student> allStudents, GradeManager gradeManager, BatchConfig config) {
        // 1. Filter Students based on Scope
        List<Student> targetStudents = filterStudents(allStudents, gradeManager, config);

        if (targetStudents.isEmpty()) {
            System.out.println("No students match the selected scope.");
            return;
        }

        // 2. Setup Execution using Config
        int threadCount = config.threadCount;
        System.out.println("\nInitializing thread pool...");
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        System.out.println("[OK] Fixed Thread Pool created: " + threadCount + " threads");
        System.out.println("\nProcessing " + targetStudents.size() + " student reports...");

        // Statistics
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicLong totalProcessingTime = new AtomicLong(0); // Sum of all task times
        final int totalTasks = targetStudents.size();
        AtomicInteger completedTasks = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        String batchId = "batch_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        // 3. UI Monitor Thread
        Thread monitorThread = new Thread(() -> {
            try {
                while (completedTasks.get() < totalTasks) {
                    printStatus(threadCount, completedTasks.get(), totalTasks);
                    Thread.sleep(100);
                }
                printStatus(threadCount, completedTasks.get(), totalTasks); // Final update
            } catch (InterruptedException e) {
                // finished
            }
        });
        monitorThread.start();

        // 4. Submit Tasks
        for (Student student : targetStudents) {
            executor.submit(() -> {
                long tId = Thread.currentThread().getId();
                long taskStart = System.currentTimeMillis();
                try {
                    // Update Status: Check-in
                    updateThreadStatus(tId, student.getStudentId(), "Processing...");

                    // Simulate varying workload
                    Thread.sleep(50 + (long) (Math.random() * 200));

                    // Generate Reports based on Format
                    generateReports(student, gradeManager, config.format, batchId);

                    long duration = System.currentTimeMillis() - taskStart;
                    totalProcessingTime.addAndGet(duration);
                    successCount.incrementAndGet();

                    // Update Status: Done
                    updateThreadStatus(tId, student.getStudentId(), "[OK] (" + duration + "ms)");

                } catch (Exception e) {
                    failCount.incrementAndGet();
                    updateThreadStatus(tId, student.getStudentId(), "[X] Failed");
                    Logger.logError("Batch error for " + student.getStudentId(), e);
                } finally {
                    completedTasks.incrementAndGet();
                }
            });
        }

        // 5. Await Completion
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
            monitorThread.join(); // Ensure monitor finishes printing
        } catch (InterruptedException e) {
            System.out.println("\nBatch interrupted.");
        }

        long endTime = System.currentTimeMillis();
        long totalWallTime = endTime - startTime;

        // 6. Final Summary
        printSummary(totalTasks, successCount.get(), failCount.get(), totalWallTime, totalProcessingTime.get(),
                threadCount, batchId);
    }

    private void updateThreadStatus(long threadId, String studentId, String status) {
        String display = String.format("%s ... %s", studentId, status);
        threadStatus.put(threadId, display);
    }

    private void printStatus(int threadCount, int completed, int total) {
        System.out.print("\033[H\033[2J"); // ANSI Clear Screen

        StringBuilder sb = new StringBuilder();
        sb.append("\nBATCH PROCESSING STATUS\n");
        sb.append("__________________________________________________\n");

        // Simple Progress Bar
        int barLength = 20;
        double progressRatio = total > 0 ? (double) completed / total : 0;
        int percent = (int) (progressRatio * 100);
        int filled = (int) (progressRatio * barLength);

        sb.append("Progress: [");
        for (int i = 0; i < barLength; i++) {
            if (i < filled)
                sb.append("█");
            else
                sb.append("░");
        }
        sb.append("] " + percent + "% (" + completed + "/" + total + " completed)\n");

        System.out.print("\r" + sb.toString());
    }

    private List<Student> filterStudents(List<Student> all, GradeManager gm, BatchConfig config) {
        List<Student> filtered = new ArrayList<>();
        switch (config.scope) {
            case ALL:
                filtered.addAll(all);
                break;
            case TYPE:
                for (Student s : all) {
                    if (s.getStudentId().startsWith("HON") && config.filterValue.equalsIgnoreCase("Honors"))
                        filtered.add(s);
                    else if (s.getStudentType().equalsIgnoreCase(config.filterValue))
                        filtered.add(s);
                }
                break;
            case GRADE_RANGE:
                try {
                    double threshold = Double.parseDouble(config.filterValue.replace(">", "").trim());
                    for (Student s : all) {
                        if (gm.calculateOverallAverage(s.getStudentId()) >= threshold)
                            filtered.add(s);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid grade range format.");
                }
                break;
            case CUSTOM:
                String[] ids = config.filterValue.split(",");
                for (String id : ids) {
                    try {
                        String cleanId = id.trim();
                        Student s = all.stream().filter(st -> st.getStudentId().equals(cleanId)).findFirst()
                                .orElse(null);
                        if (s != null)
                            filtered.add(s);
                    } catch (Exception e) {
                    }
                }
                break;
        }
        return filtered;
    }

    private void generateReports(Student s, GradeManager gm, ReportFormat format, String batchId) throws IOException {
        String baseDir = "reports/" + batchId + "/";
        String safeName = s.getStudentId();

        if (format == ReportFormat.TEXT || format == ReportFormat.ALL) {
            String content = reportGenerator.generateDetailedReport(s, gm);
            fileExporter.exportToPath(baseDir + safeName + ".txt", content);
        }
        if (format == ReportFormat.PDF || format == ReportFormat.ALL) {
            fileExporter.exportToPath(baseDir + safeName + ".pdf",
                    "[PDF HEADER]\n" + reportGenerator.generateSummaryReport(s, gm));
        }
        if (format == ReportFormat.EXCEL || format == ReportFormat.ALL) {
            String csv = s.getStudentId() + "," + s.getName() + "," + gm.calculateOverallAverage(s.getStudentId());
            fileExporter.exportToPath(baseDir + safeName + ".csv", csv);
        }
    }

    private void printSummary(int total, int success, int failed, long wallTime, long procTime, int threads,
            String batchId) {
        System.out.println("\n\n[OK] BATCH GENERATION COMPLETED!");
        System.out.println("\nEXECUTION SUMMARY");
        System.out.println("__________________________________________________");
        System.out.println("Total Reports: " + total);
        System.out.println("Successful:    " + success);
        System.out.println("Failed:        " + failed);
        System.out.println("Total Time:    " + (wallTime / 1000.0) + " seconds");
        double avgTime = total > 0 ? (procTime / (double) total) : 0;
        System.out.println("Avg Time/Rep:  " + String.format("%.0f", avgTime) + "ms");

        long estimatedSeq = (long) avgTime * total;
        System.out.println("\nSequential Processing (estimated): " + (estimatedSeq / 1000.0) + " seconds");
        System.out.println("Concurrent Processing (actual):    " + (wallTime / 1000.0) + " seconds");
        if (wallTime > 0)
            System.out.println(
                    "Performance Gain: " + String.format("%.1fx", (double) estimatedSeq / wallTime) + " faster");

        System.out.println("\nThread Pool Statistics:");
        System.out.println("  Configured Threads: " + threads);

        System.out.println("\nOutput Location: ./reports/" + batchId + "/");
        System.out.println("Total Files Generated: " + (success * (threads > 1 ? 1 : 1)));
    }
}
