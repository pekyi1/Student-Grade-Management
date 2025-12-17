# Project Objectives Implementation Mapping

This document maps the core project objectives to their concrete implementations within the codebase. It details where each objective is realized, how it works, and provides examples from the code.

## 1. Design and Implement Type-Safe Data Structures

**Objective**: Use Java Collections Framework and generics to efficiently manage data, selecting optimal types based on performance.

### Implementation Overview
The project heavily utilizes the Java Collections Framework to optimize data access and manipulation. Specific collection types were chosen based on their time complexity for required operations (e.g., O(1) lookups vs. O(n) iteration).

### Key Code Locations
*   **`src/services/StudentManager.java`**
    *   **Usage**: `private java.util.Map<String, Student> students;`
    *   **Explanation**: A `HashMap` is used to store students keyed by their ID. This allows for **O(1)** (constant time) retrieval of any student, which is significantly faster than iterating through a list (O(n)) for large datasets.
    *   **Generics**: The code uses `<String, Student>` ensuring type safety; only Strings can be keys and only Student objects can be values.

*   **`src/services/GradeManager.java`**
    *   **Usage**: `private java.util.LinkedList<Grade> grades;`
    *   **Explanation**: A `LinkedList` is used for storing grades. This is beneficial if the primary operation involves adding elements to the end or beginning frequently, or if we need to iterate in reverse order efficiently (using `descendingIterator()`).
    *   **Usage**: `Set<models.Subject> subjects = new HashSet<>();`
    *   **Explanation**: A `HashSet` is used to collect unique subjects. This automatically filters out duplicates, ensuring the list of subjects contains no repetitions.

*   **`src/services/StudentManager.java` (Sorting)**
    *   **Usage**: `java.util.TreeMap<Double, List<Student>> gpaMap = new java.util.TreeMap<>(java.util.Collections.reverseOrder());`
    *   **Explanation**: A `TreeMap` is used to automatically sort students by their GPA. The `Collections.reverseOrder()` ensures the highest GPA comes first.

### Code Example
```java
// From StudentManager.java
public Student findStudent(String studentId) {
    // O(1) lookup performance
    return students.get(studentId); 
}
```

---

## 2. Implement Modern File I/O Operations

**Objective**: Use NIO.2 API and Stream processing to handle multiple file formats (CSV, JSON, Binary) with resource management.

### Implementation Overview
The `BulkImportService` demonstrates advanced I/O capabilities. It moves beyond standard `BufferedReader` by using `java.nio.file.Files` lines streaming, which allows processing large files without loading the entire file into memory.

### Key Code Locations
*   **`src/services/BulkImportService.java`**
    *   **Streaming CSV**: Uses `Files.lines(path)` to create a `Stream<String>`. This is lazy-loaded, meaning lines are read one by one, keeping memory usage low.
    *   **Multiple Formats**: The `importGrades` method detects file extensions (`.csv`, `.json`, `.dat`) and delegates to specific handlers (`importCSVStreaming`, `importJSON`, `importBinary`).
    *   **Resource Management**: Uses try-with-resources (`try (Stream<String> lines = ...)`) to ensure file handles are automatically closed even if errors occur.

### Code Example
```java
// From BulkImportService.java
private void importCSVStreaming(Path path, ...) {
    // Try-with-resources ensures the stream acts as a finite resource and closes
    try (Stream<String> lines = Files.lines(path)) { 
        lines.forEach(line -> {
            // Process line...
        });
    } catch (IOException e) { ... }
}
```

---

## 3. Create Comprehensive Input Validation

**Objective**: Use regular expressions (Regex) to ensure data integrity for IDs, emails, phones, etc.

### Implementation Overview
Validation logic is centralized in a utility class `ValidationUtils`. This promotes the "Don't Repeat Yourself" (DRY) principle and ensures consistent validation rules across the application.

### Key Code Locations
*   **`src/utils/ValidationUtils.java`**
    *   **Regex Patterns**: Defines `static final Pattern` constants (compiled once for performance).
    *   **Patterns Implemented**:
        *   `STUDENT_ID_PATTERN`: `^STU\d{3}$` (Matches "STU" followed by 3 digits).
        *   `EMAIL_PATTERN`: Validates standard email formats.
        *   `PHONE_PATTERN`: complex regex handling multiple formats like `(123) 456-7890`.

*   **`src/services/StudentManager.java`**
    *   **Usage**: Calls `ValidationUtils.validateEmail(...)` before adding a student. If validation fails, a custom `InvalidDataException` is thrown, preventing bad data from entering the system.

### Code Example
```java
// From ValidationUtils.java
private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^STU\\d{3}$");

public static void validateStudentId(String id) throws InvalidDataException {
    if (id == null || !STUDENT_ID_PATTERN.matcher(id).matches()) {
        throw new InvalidDataException("Invalid Student ID format...");
    }
}
```

---

## 4. Design and Implement Thread-Safe Concurrent Operations

**Objective**: Handle background tasks and multi-threaded operations safely using synchronization and the Executor framework.

### Implementation Overview
The application uses the `ExecutorService` to manage pools of background threads. It also protects shared resources (like the `grades` list) using `synchronized` blocks to prevent race conditions (data corruption when two threads write at the same time).

### Key Code Locations
*   **`src/services/StatisticsDashboardService.java`**
    *   **Executor Framework**: Uses `Executors.newScheduledThreadPool(2)` to create a pool of threads that can run tasks periodically (e.g., auto-refreshing the dashboard every 5 seconds).
    *   **Atomicity**: Uses `AtomicBoolean` (`isPaused`, `isRunning`) to managing flags across threads without explicit locking.

*   **`src/services/GradeManager.java`**
    *   **Synchronization**: The `addGrade` method wraps the critical section in `synchronized (grades) { ... }`. This ensures that if the dashboard reads grades while an import is adding grades, the data stays consistent.

*   **`src/services/TaskScheduler.java`**
    *   **Background Tasks**: Manages a list of scheduled tasks using `ScheduledFuture`.

### Code Example
```java
// From StatisticsDashboardService.java
private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

// From GradeManager.java
public void addGrade(Grade grade) {
    synchronized (grades) { 
        // Critical section: Only one thread can execute this at a time
        grades.add(grade);
        saveGrades();
    }
}
```

---

## 5. Optimize Application Performance

**Objective**: Analyze performance, implement efficient access patterns, and ensure thread safety.

### Implementation Overview
Performance is addressed through algorithmic choices (O(1) maps), caching strategies, and verified through dedicated benchmarks.

### Key Code Locations
*   **`src/services/GradeManager.java` (Caching)**
    *   **`CacheService`**: The system specifically caches student averages. Calculating an average requires iterating all grades (O(N)). By caching the result, subsequent requests are O(1), drastically reducing CPU load for the dashboard. The cache is invalidated whenever a new grade is added.

*   **`src/PerformanceTest.java`**
    *   **Verification**: Contains specific benchmarks:
        *   **HashMap vs ArrayList**: Proves the O(1) lookup speedup.
        *   **Stream Memory**: Compares memory usage of streaming vs loading all lines.
        *   **Thread Safety**: Simulates high-concurrency scenarios (reading + writing simultaneously) to ensure no crashes occur.

### Code Example
```java
// From GradeManager.java
public double calculateOverallAverage(String studentId) {
    // Check cache first (Performance Optimization)
    Double cachedAvg = averageCache.get(studentId);
    if (cachedAvg != null) {
        return cachedAvg;
    }
    // ... calculate ...
    averageCache.put(studentId, avg); // Store result
}
```
