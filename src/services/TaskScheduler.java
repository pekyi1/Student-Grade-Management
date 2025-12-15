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

    public void scheduleTask(String name, Runnable task, long initialDelay, long period, TimeUnit unit) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                task.run();
                System.out.println("[Scheduler] Task '" + name + "' executed successfully.");
            } catch (Exception e) {
                System.err.println("[Scheduler] Task '" + name + "' failed: " + e.getMessage());
            }
        }, initialDelay, period, unit);
        
        activeTasks.add(new ScheduledTaskInfo(name, period, unit, future));
        saveSchedules();
    }

    public List<ScheduledTaskInfo> getActiveTasks() {
        return new ArrayList<>(activeTasks); // Return copy
    }

    public void shutdown() {
        System.out.println("Shutting down Task Scheduler...");
        scheduler.shutdown();
    }

    private void saveSchedules() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCHEDULES_FILE))) {
            // We can't serialize Runnable or Future easily. 
            // We serialize the configuration (name, period, unit) to recreate generic tasks on restart if needed.
            // But specific task logic (lambdas) needs to be re-mapped. 
            // For this US, we likely just want ID/Name persistence or basic config.
            // We will save a clean list of DTOs.
            List<TaskConfig> configs = new ArrayList<>();
            for(ScheduledTaskInfo info : activeTasks) {
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
        if(!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
             List<TaskConfig> configs = (List<TaskConfig>) ois.readObject();
             System.out.println("Restoring " + configs.size() + " schedules...");
             for(TaskConfig config : configs) {
                 // Re-attach logic based on name convention or just generic logging if unknown
                 Runnable logic = () -> System.out.println("Restored generic task: " + config.getName());
                 
                 if(config.getName().contains("Backup")) {
                     logic = AutomatedTasks.createDailyBackupTask();
                 }
                 
                 // Reschedule without saving again (avoid dupe or recursive save loop if scheduleTask calls save)
                 // We need a internal schedule method or just use scheduleTask provided we handle dupes?
                 // Simple approach: clear list before load or check existence. 
                 // Here we just schedule.
                 
                 // NOTE: We deviate slightly from scheduleTask calling saveSchedules to avoid overwrite race? 
                 // Actually scheduleTask appends. 
                 // We should probably NOT call scheduleTask here but the internal logic.
                 // Refactor for cleaner separation if time allows. 
                 // For now, let's just schedule and it will re-save the list which is fine.
                 
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

        public ScheduledTaskInfo(String name, long period, TimeUnit unit, ScheduledFuture<?> future) {
            this.name = name;
            this.period = period;
            this.unit = unit;
            this.future = future;
        }
        
        // Getters
        public String getName() { return name; }
        public long getPeriod() { return period; }
        public TimeUnit getUnit() { return unit; }
        public long getDelay(TimeUnit u) { return future.getDelay(u); }
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
        
        public String getName() { return name; }
        public long getPeriod() { return period; }
        public TimeUnit getUnit() { return unit; }
    }
}
