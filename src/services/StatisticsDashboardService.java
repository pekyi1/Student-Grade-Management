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

    public void startDashboard(StudentManager studentManager, GradeManager gradeManager, TaskScheduler taskScheduler,
            Scanner scanner) {
        isRunning.set(true);
        System.out.println("Starting Real-Time Dashboard...");

        // Start background update task (every 5 seconds)
        autoRefreshTask = scheduler.scheduleAtFixedRate(() -> {
            if (!isPaused.get() && isRunning.get()) {
                refreshDashboard(studentManager, gradeManager, taskScheduler);
                printDashboard();
            }
        }, 0, 5, TimeUnit.SECONDS);

        // Input loop on main thread
        handleInput(scanner, studentManager, gradeManager, taskScheduler);
    }

    private void handleInput(Scanner scanner, StudentManager sm, GradeManager gm, TaskScheduler ts) {
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
                        refreshDashboard(sm, gm, ts);
                        printDashboard();
                        break;
                    default:
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
    }

    private synchronized void refreshDashboard(StudentManager sm, GradeManager gm, TaskScheduler ts) {
        // Reload data from disk to pick up changes from other processes
        sm.loadStudents();
        gm.loadGrades();

        List<Grade> grades = gm.getAllGrades();
        List<Student> students = sm.getAllStudents();

        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("\n\n\n\n\n\n"); // "Clear" screen
        sb.append("REAL-TIME STATISTICS DASHBOARD\n");
        sb.append("__________________________________________________\n");
        sb.append(String.format("Auto-refresh: %-8s (5 sec) | Thread: %s\n",
                isPaused.get() ? "PAUSED" : "Enabled",
                "RUNNING"));
        sb.append("Press 'Q' to quit | 'R' to refresh now | 'P' to pause\n");
        sb.append("__________________________________________________\n");

        lastUpdated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        sb.append("Last Updated: ").append(lastUpdated).append("\n\n");

        // System Status
        sb.append("SYSTEM STATUS\n");
        sb.append("__________________________________________________\n");
        sb.append(String.format("Total Students:  %-5d\n", students.size()));
        sb.append(String.format("Active Threads:  %-5d\n", Thread.activeCount()));
        sb.append(String.format("Cache Hit Rate:  %.1f%%\n", gm.getCacheService().getHitRate()));
        long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        sb.append(String.format("Memory Usage:    %d MB / %d MB\n\n", (totalMem - freeMem), totalMem));

        // Live Statistics
        sb.append("LIVE STATISTICS\n");
        sb.append("__________________________________________________\n");
        sb.append(String.format("Total Grades:    %-5d\n", grades.size()));

        // Grade Distribution
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

        sb.append("\nGrade Distribution (Live):\n");
        String[] labels = { "90-100% (A):", "80-89%  (B):", "70-79%  (C):", "60-69%  (D):", "0-59%   (F):" };

        for (int i = 0; i < 5; i++) {
            double percent = grades.isEmpty() ? 0 : (double) counts[i] / grades.size() * 100;
            int barLength = (int) (percent / 2); // 50 chars = 100%
            String bar = repeat("█", barLength) + repeat("░", 50 - barLength);
            sb.append(String.format("%-12s [%s] %.1f%% (%d grades)%n", labels[i], bar, percent, counts[i]));
        }

        // Current Statistics
        if (!grades.isEmpty()) {
            double sum = 0;
            for (Grade g : grades)
                sum += g.getGrade();
            double mean = sum / grades.size();

            // Median
            grades.sort((g1, g2) -> Double.compare(g1.getGrade(), g2.getGrade()));
            double median = grades.get(grades.size() / 2).getGrade();

            sb.append(String.format("\nCurrent Statistics:\n"));
            sb.append(String.format("Mean:   %.1f%%\n", mean));
            sb.append(String.format("Median: %.1f%%\n", median));
        }

        // Top Performers
        sb.append("\nTop Performers (Live Rankings):\n");
        students.stream()
                .sorted((s1, s2) -> Double.compare(gm.calculateOverallAverage(s2.getStudentId()),
                        gm.calculateOverallAverage(s1.getStudentId())))
                .limit(3)
                .forEach(s -> {
                    double avg = gm.calculateOverallAverage(s.getStudentId());
                    double gpa = new GPACalculator().calculateCumulativeGPA(gm.getGradesForStudent(s.getStudentId()));
                    sb.append(String.format("- %-6s %-20s : GPA %.2f (%.1f%%)\n",
                            s.getStudentId(), s.getName(), gpa, avg));
                });

        // Concurrent Operations
        sb.append("\nCONCURRENT OPERATIONS\n");
        sb.append("__________________________________________________\n");
        List<services.TaskScheduler.ScheduledTaskInfo> tasks = ts.getActiveTasks();
        if (tasks.isEmpty()) {
            sb.append("[No background tasks scheduled]\n");
        } else {
            for (services.TaskScheduler.ScheduledTaskInfo task : tasks) {
                long delay = task.getDelay(TimeUnit.SECONDS);
                String status = delay <= 0 ? "RUNNING" : "WAITING (" + delay + "s)";
                sb.append(String.format("[%-15s] : %s\n", task.getName(), status));
            }
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
