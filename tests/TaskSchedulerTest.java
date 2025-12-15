
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import services.TaskScheduler;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class TaskSchedulerTest {
    private TaskScheduler scheduler;

    @BeforeEach
    public void setup() {
        scheduler = new TaskScheduler();
    }

    @AfterEach
    public void tearDown() {
        scheduler.shutdown();
        new File("data/schedules.dat").delete();
    }

    @Test
    public void testScheduleAndRetrieveTask() {
        System.out.println("Starting testScheduleAndRetrieveTask");
        scheduler.scheduleTask("Test Task", () -> {
        }, 0, 1, TimeUnit.HOURS);

        List<TaskScheduler.ScheduledTaskInfo> tasks = scheduler.getActiveTasks();
        System.out.println("Tasks retrieved: " + tasks.size());
        assertFalse(tasks.isEmpty(), "Tasks should not be empty");
        assertEquals("Test Task", tasks.get(0).getName());
    }

    @Test
    public void testPersistence() {
        // Schedule task and save (implied by scheduleTask)
        scheduler.scheduleTask("Persistent Task", () -> {
        }, 0, 1, TimeUnit.MINUTES);

        // Assert file exists
        File file = new File("data/schedules.dat");
        assertTrue(file.exists(), "Schedule file should exist");

        // Reload new scheduler
        scheduler.shutdown();
        scheduler = new TaskScheduler();
        scheduler.loadSchedules(null, null);

        List<TaskScheduler.ScheduledTaskInfo> tasks = scheduler.getActiveTasks();
        assertFalse(tasks.isEmpty(), "Should have reloaded tasks");
        assertEquals("Persistent Task", tasks.get(0).getName());
    }
}
