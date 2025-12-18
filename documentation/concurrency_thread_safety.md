# Thread Safety Documentation

## 1. Synchronization Strategy
The system uses a mix of Coarse-Grained Synchronization and Concurrent Collections to ensure data integrity.

### synchronized Blocks
*   **`GradeManager.grades`**: All access to the master grade list is guarded by `synchronized(grades)`.
    *   *Examples:* `addGrade()`, `getAllUniqueSubjects()`.
    *   *Purpose:* Prevents `ConcurrentModificationException` when iteration and modification happen simultaneously.

### Concurrent Collections & Atomics
*   **`CacheService`**: Uses `Collections.synchronizedMap` (or designed for `ConcurrentHashMap`) to allow safe concurrent reads/writes to the cache.
*   **`StatisticsDashboardService`**:
    *   `AtomicBoolean isRunning`: Flags for thread control.
    *   `volatile String currentDashboardView`: Ensures memory visibility of the rendered string across the update thread and the UI thread.
*   **`AuditLogService`**: (Planned/Implemented) uses `ConcurrentLinkedQueue` for non-blocking logging.

## 2. Thread Pools (ExecutorService)
Managed via `App` and `TaskScheduler`.

*   **FixedThreadPool**: Used for Batch Report Generation to limit concurrent I/O operations (e.g., 2-8 threads).
*   **ScheduledExecutorService**: Used for accurate periodic task execution (Dashboard refresh, Daily GPA calc) without blocking the main thread.
*   **Shutdown Policy**: Uses `shutdownNow()` in shutdown hooks to ensure clean application exit.

## 3. Verification
A `ConcurrencyStressTest` was developed to simulate:
1.  **Reader Thread**: Dashboard sorting students.
2.  **Writer Thread**: Adding grades.
3.  **Reader Thread**: Generating reports.

**Result:** The synchronized blocks in `GradeManager` successfully prevented data corruption and exceptions under high load.
