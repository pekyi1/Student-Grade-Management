package services;

import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Manages scheduled tasks using a PriorityQueue to ensure high-priority tasks
 * run first.
 * US-1: Use PriorityQueue for task scheduling based on priority.
 */
public class TaskScheduler {
    private PriorityQueue<ScheduledTask> taskQueue;

    public TaskScheduler() {
        // PriorityQueue orders elements according to their natural ordering or a
        // Comparator.
        // We want higher priority (lower number or higher enum) to come first.
        // Assuming Priority Level 1 is highest.
        this.taskQueue = new PriorityQueue<>(Comparator.comparingInt(ScheduledTask::getPriority));
    }

    /**
     * Adds a task to the scheduler.
     * Time Complexity: O(log n) - PriorityQueue insertion.
     * 
     * @param task The task to add.
     */
    public void addTask(ScheduledTask task) {
        taskQueue.offer(task);
        System.out.println("Task scheduled: " + task.getDescription() + " (Priority: " + task.getPriority() + ")");
    }

    /**
     * Retrieves and removes the highest priority task.
     * Time Complexity: O(log n) - Removal from PriorityQueue.
     * 
     * @return The next task to execute, or null if empty.
     */
    public ScheduledTask pollNextTask() {
        return taskQueue.poll();
    }

    /**
     * Peeks at the highest priority task without removing it.
     * Time Complexity: O(1)
     */
    public ScheduledTask peekNextTask() {
        return taskQueue.peek();
    }

    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }

    public int getTaskCount() {
        return taskQueue.size();
    }

    // Inner class for Task
    public static class ScheduledTask {
        private String description;
        private int priority; // 1 = High, 10 = Low

        public ScheduledTask(String description, int priority) {
            this.description = description;
            this.priority = priority;
        }

        public String getDescription() {
            return description;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public String toString() {
            return "[" + priority + "] " + description;
        }
    }
}
