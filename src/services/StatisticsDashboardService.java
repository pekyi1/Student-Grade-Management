package services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import models.ClassStatistics;
import models.Grade;
import models.Student;

public class StatisticsDashboardService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> autoRefreshTask;
    private final ClassStatistics classStatistics;
    private final AtomicBoolean isPaused = new AtomicBoolean(false);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private volatile String lastUpdated = "N/A";

    // Cache for stats to avoid flickering or recalculation on every paint if data
    // hasn't changed
    private volatile String currentDashboardView = "";

    public StatisticsDashboardService() {
        this.classStatistics = new ClassStatistics();
    }

    public void startDashboard(StudentManager studentManager, GradeManager gradeManager, Scanner scanner) {
        isRunning.set(true);
        System.out.println("Starting Real-Time Dashboard...");

        // Start background update task (every 5 seconds)
        autoRefreshTask = scheduler.scheduleAtFixedRate(() -> {
            if (!isPaused.get() && isRunning.get()) {
                refreshDashboard(studentManager, gradeManager);
                // We don't print here directly to avoid messing up input line?
                // Actually, the requirements say "Auto-refreshing dashboard".
                // In a console app, this usually means clearing screen or re-printing.
                // We will print. The user input will just happen at the bottom.
                printDashboard();
            }
        }, 0, 5, TimeUnit.SECONDS);

        // Input loop on main thread
        handleInput(scanner, studentManager, gradeManager);
    }

    private void handleInput(Scanner scanner, StudentManager sm, GradeManager gm) {
        while (isRunning.get()) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim().toUpperCase();
                switch (input) {
                    case "Q":
                        stopDashboard();
                        return;
                    case "P":
                        boolean paused = !isPaused.get();
                        isPaused.set(paused);
                        System.out.println(paused ? "Dashboard PAUSED." : "Dashboard RESUMED.");
                        break;
                    case "R":
                        System.out.println("Refreshing...");
                        refreshDashboard(sm, gm);
                        printDashboard();
                        break;
                    default:
                        // Ignore other input or print help
                        break;
                }
            }
        }
    }

    public void stopDashboard() {
        isRunning.set(false);
        if (autoRefreshTask != null) {
            autoRefreshTask.cancel(true);
        }
        // Don't shutdown executor if we want to reuse service?
        // Instructions imply cleanly shutting down thread pool.
        // We might want to shutdown if exiting the feature.
        // But if we re-enter, we need it.
        // Let's rely on App shutdown for full pool shutdown or managing it closer.
        // For now, cancel task is enough to stop updates.
    }

    private void refreshDashboard(StudentManager sm, GradeManager gm) {
        List<Grade> grades = gm.getAllGrades();
        List<Student> students = java.util.Arrays.asList(sm.getAllStudents());

        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("\n\n\n\n\n\n"); // "Clear" screen by pushing content up
        sb.append("REAL-TIME STATISTICS DASHBOARD\n");
        sb.append("__________________________________________________\n");
        sb.append(String.format("Auto-refresh: %s (5 sec) | Thread: %s\n",
                isPaused.get() ? "PAUSED" : "Enabled",
                Thread.currentThread().getName()));
        sb.append("Press 'Q' to quit | 'R' to refresh now | 'P' to pause\n");
        sb.append("__________________________________________________\n");

        lastUpdated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        sb.append("Last Updated: ").append(lastUpdated).append("\n\n");

        // System Status
        sb.append("SYSTEM STATUS\n");
        sb.append("__________________________________________________\n");
        sb.append("Total Students: ").append(students.size()).append("\n");
        // Mocking some system metrics as we don't have deep hooks yet
        sb.append("Active Threads: ").append(Thread.activeCount()).append("\n");
        sb.append("Cache Hit Rate: ").append("N/A").append("\n"); // Placeholder for future US
        sb.append("Memory Usage: ")
                .append((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024)
                .append(" MB / ").append(Runtime.getRuntime().totalMemory() / 1024 / 1024).append(" MB\n\n");

        // Live Statistics
        sb.append("LIVE STATISTICS\n");
        sb.append("__________________________________________________\n");
        sb.append("Total Grades: ").append(grades.size()).append("\n");

        // Adding distribution chart logic here (reusing logic from ClassStatistics
        // usually better, allowing duplication for custom UI format)
        int[] counts = new int[5]; // A, B, C, D, F
        for (Grade g : grades) {
            if (g.getGrade() >= 90)
                counts[0]++;
            else if (g.getGrade() >= 80)
                counts[1]++;
            else if (g.getGrade() >= 70)
                counts[2]++;
            else if (g.getGrade() >= 60)
                counts[3]++;
            else
                counts[4]++;
        }

        sb.append("Grade Distribution (Live):\n");
        String[] labels = { "90-100% (A):", "80-89%  (B):", "70-79%  (C):", "60-69%  (D):", "0-59%   (F):" };
        int maxCount = 0;
        for (int c : counts)
            if (c > maxCount)
                maxCount = c;

        for (int i = 0; i < 5; i++) {
            double percent = grades.isEmpty() ? 0 : (double) counts[i] / grades.size() * 100;
            int barLength = (int) (percent / 2);
            String bar = repeat("█", barLength) + repeat("░", 50 - barLength);
            sb.append(String.format("%-12s %s %.1f%% (%d grades)%n", labels[i], bar, percent, counts[i]));
        }

        sb.append("\nCommand: ");

        currentDashboardView = sb.toString();
    }

    private void printDashboard() {
        System.out.print(currentDashboardView);
    }

    private String repeat(String s, int n) {
        if (n <= 0)
            return "";
        return new String(new char[n]).replace("\0", s);
    }

    // Shutdown hook for app exit
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
