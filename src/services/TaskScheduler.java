package services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.io.*;

public class TaskScheduler {
    private final ScheduledExecutorService scheduler;
    private final List<ScheduledTaskInfo> activeTasks;
    private static final String SCHEDULES_FILE = "data/schedules.dat";

    public TaskScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(3);
        this.activeTasks = new ArrayList<>();
        createDataDir();
    }

    private void createDataDir() {
        new File("data").mkdirs();
    }

    private AuditLogService auditService;

    public void setAuditService(AuditLogService service) {
        this.auditService = service;
    }

    public void scheduleTask(String name, Runnable task, long initialDelay, long period, TimeUnit unit) {
        ScheduledTaskInfo info = new ScheduledTaskInfo(name, period, unit, null);

        Runnable wrappedTask = () -> {
            info.markRunning();
            long start = System.currentTimeMillis();
            try {
                task.run();
                long duration = System.currentTimeMillis() - start;
                info.markSuccess();
                System.out.println("[Scheduler] Task '" + name + "' executed successfully.");
                if (auditService != null) {
                    auditService.log("TASK_EXEC", "Task: " + name, "SCHEDULER", true, duration);
                }
            } catch (Exception e) {
                info.markFailure();
                long duration = System.currentTimeMillis() - start;
                System.err.println("[Scheduler] Task '" + name + "' failed: " + e.getMessage());
                if (auditService != null) {
                    auditService.log("TASK_EXEC", "Task: " + name + " Failed: " + e.getMessage(), "SCHEDULER", false,
                            duration);
                }
            }
        };

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(wrappedTask, initialDelay, period, unit);
        info.setFuture(future);

        activeTasks.add(info);
        saveSchedules();
    }

    public List<ScheduledTaskInfo> getActiveTasks() {
        return new ArrayList<>(activeTasks); // Return copy
    }

    public String getSchedulerStats() {
        int totalExecutions = 0;
        int totalSuccess = 0;

        for (ScheduledTaskInfo info : activeTasks) {
            totalExecutions += info.getSubmissionCount();
            totalSuccess += info.getSuccessCount();
        }

        if (totalExecutions == 0) {
            return "No executions yet";
        }

        double rate = (double) totalSuccess / totalExecutions * 100;
        return String.format("Success Rate: %.0f%% | Executions: %d", rate, totalExecutions);
    }

    // ... existing shutdown/save/load methods ...
    public void shutdown() {
        System.out.println("Shutting down Task Scheduler...");
        scheduler.shutdown();
    }

    private void saveSchedules() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCHEDULES_FILE))) {
            List<TaskConfig> configs = new ArrayList<>();
            for (ScheduledTaskInfo info : activeTasks) {
                configs.add(new TaskConfig(info.getName(), info.getPeriod(), info.getUnit()));
            }
            oos.writeObject(configs);
        } catch (IOException e) {
            System.err.println("Failed to save schedules: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadSchedules(StudentManager sm, GradeManager gm) {
        File file = new File(SCHEDULES_FILE);
        if (!file.exists())
            return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<TaskConfig> configs = (List<TaskConfig>) ois.readObject();
            System.out.println("Restoring " + configs.size() + " schedules...");
            for (TaskConfig config : configs) {
                Runnable logic = () -> System.out.println("Restored generic task: " + config.getName());

                // Simple restoration logic for demo purposes
                if (config.getName().contains("Backup")) {
                    logic = AutomatedTasks.createDailyBackupTask();
                } else if (config.getName().contains("Statistics")) {
                    // logic = ... (needs access to services, tricky in simple restore)
                }

                scheduleTask(config.getName(), logic, 5, config.getPeriod(), config.getUnit());
            }
        } catch (Exception e) {
            System.err.println("Failed to load schedules: " + e.getMessage());
        }
    }

    // Inner classes
    public static class ScheduledTaskInfo {
        private String name;
        private long period;
        private TimeUnit unit;
        private ScheduledFuture<?> future;

        // Metadata
        private String status = "PENDING";
        private java.time.LocalDateTime lastRun;
        private int executionCount = 0;
        private int successCount = 0;

        public ScheduledTaskInfo(String name, long period, TimeUnit unit, ScheduledFuture<?> future) {
            this.name = name;
            this.period = period;
            this.unit = unit;
            this.future = future;
        }

        public void setFuture(ScheduledFuture<?> future) {
            this.future = future;
        }

        public void markRunning() {
            this.status = "RUNNING";
        }

        public void markSuccess() {
            this.status = "✓ Success";
            this.lastRun = java.time.LocalDateTime.now();
            this.executionCount++;
            this.successCount++;
        }

        public void markFailure() {
            this.status = "X Failed";
            this.lastRun = java.time.LocalDateTime.now();
            this.executionCount++; // Count attempts?
        }

        public int getSubmissionCount() {
            return executionCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        // Getters
        public String getName() {
            return name;
        }

        public long getPeriod() {
            return period;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public String getStatus() {
            return status;
        }

        public String getLastRun() {
            return lastRun == null ? "Never"
                    : lastRun.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        public java.time.LocalDateTime getNextRunTime() {
            long delay = getDelay(TimeUnit.SECONDS);
            return java.time.LocalDateTime.now().plusSeconds(delay);
        }

        public long getDelay(TimeUnit u) {
            return future.getDelay(u);
        }
    }

    // Serializable DTO
    public static class TaskConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private long period;
        private TimeUnit unit;

        public TaskConfig(String name, long period, TimeUnit unit) {
            this.name = name;
            this.period = period;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public long getPeriod() {
            return period;
        }

        public TimeUnit getUnit() {
            return unit;
        }
    }
}
