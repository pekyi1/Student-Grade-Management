# Student Grade Management System (Java Advanced)

**Complexity:** Advanced
**Time Estimate:** 6-7 hours

---

## Objectives
By completing this project, you will be able to:
* Design and implement type-safe data structures using Java Collections Framework and generics to efficiently manage complex student and grade data.
* Implement modern file I/O operations using NIO.2 API and Stream processing to handle multiple file formats (CSV, JSON, binary).
* Create comprehensive input validation using regular expressions to validate student IDs, email addresses, phone numbers, and other structured data formats.
* Design and implement thread-safe concurrent operations for background tasks such as batch processing, automated report generation, and real-time statistics updates.
* Optimize application performance by analyzing collection performance characteristics and ensuring thread safety.

---

## Project Overview
You will build an enterprise-grade Student Grade Management System with the following advanced features.

### New Features
1.  **Advanced Data Management:** Replace basic data structures with optimized collections (HashMap for O(1) lookup, TreeMap for sorted reports, HashSet for unique tracking).
2.  **Multi-Format File Support:** Import/export data in CSV, JSON, and binary formats using NIO.2 with streaming.
3.  **Regex-Based Validation:** Comprehensive validation for emails, phone numbers, IDs, and custom formats.
4.  **Concurrent Report Generation:** Generate multiple student reports simultaneously using thread pools.
5.  **Real-Time Statistics Dashboard:** Background thread continuously updating class statistics safely.
6.  **Automated Grade Processing:** Scheduled tasks for periodic calculations and notifications.
7.  **Advanced Search with Regex:** Pattern-based search supporting complex queries.
8.  **Batch Operations:** Process multiple operations concurrently.
9.  **Data Caching System:** Thread-safe cache for frequently accessed data with automatic invalidation.
10. **Audit Trail:** Concurrent logging of all operations with timestamps.

### Technical Enhancements
* **Collections Optimization:** Strategic use of `ArrayList`, `LinkedList`, `HashMap`, `TreeMap`, `HashSet`, `TreeSet`.
* **Stream Processing:** Functional operations for filtering, mapping, and aggregation.
* **NIO.2 File Operations:** Modern file handling with `Path`, `Files`, and `WatchService`.
* **Thread Safety:** Synchronized collections, `ConcurrentHashMap`, and locking mechanisms.
* **Executor Framework:** Fixed, cached, and scheduled thread pools.

---

## User Stories & Technical Requirements

### US-1: Advanced Data Management with Optimized Collections
**As a developer, I want to use optimized collection types so that the system performs efficiently with large datasets.**

**Acceptance Criteria:**
* Use `HashMap<String, Student>` for O(1) student lookup by ID.
* Use `TreeMap<Double, List<Student>>` for sorted GPA rankings.
* Use `HashSet<String>` for tracking unique courses enrolled.
* Use `ArrayList` for maintaining insertion order where needed.
* Use `LinkedList` for frequent insertions/deletions in grade history.
* Use `PriorityQueue` for task scheduling based on priority.
* Implement custom `Comparator` for sorting students by multiple criteria.
* All collection operations documented with Big-O complexity.

### US-2: Multi-Format File Operations with NIO.2
**As a teacher, I want to import and export data in multiple formats so that I can integrate with other systems and tools.**

**Acceptance Criteria:**
* Support CSV, JSON, and binary serialization formats.
* Use `java.nio.file.Files` and `Path` for all file operations.
* Implement streaming for large CSV files (don't load entire file into memory).
* Use try-with-resources for proper resource management.
* Handle file encoding (UTF-8) explicitly.
* Implement file watching with `WatchService` to detect new import files.
* Create separate directories for each format: `./data/csv/`, `./data/json/`, `./data/binary/`.

### US-3: Comprehensive Regex Input Validation
**As a system administrator, I want all user inputs validated with regex patterns so that data integrity is maintained.**

**Acceptance Criteria:**
* **Student ID Pattern:** `STU\d{3}` (STU followed by exactly 3 digits).
* **Email Pattern:** `^[a-zA-Z0-9_%+-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$`
* **Phone Pattern:** Support `(123) 456-7890`, `123-456-7890`, `+1-123-456-7890`, `1234567890`.
* **Name Pattern:** Letters, spaces, hyphens, and apostrophes only.
* **Date Pattern:** `^\d{4}-\d{2}-\d{2}$` (YYYY-MM-DD).
* **Course Code Pattern:** `^[A-Z]{3}\d{3}$` (e.g., MAT101).
* **Grade Pattern:** `^(100|[1-9]?\d)$` (0-100).
* Compile regex patterns once and reuse (`Pattern.compile()`).
* Create `ValidationUtils` class with static methods.

### US-4: Concurrent Batch Report Generation
**As a teacher, I want to generate reports for multiple students simultaneously so that I can save time.**

**Acceptance Criteria:**
* Allow selection of 2-8 concurrent threads.
* Use `ExecutorService` with `FixedThreadPool` for parallel generation.
* Display progress bar showing completion status.
* Calculate total time vs. estimated sequential time.
* Ensure thread-safe file writing.
* Properly shut down thread pool with timeout.

### US-5: Real-Time Statistics Dashboard
**As a teacher, I want to see live class statistics that update automatically so that I can monitor class performance.**

**Acceptance Criteria:**
* Launch background daemon thread that calculates statistics every 5 seconds.
* Display auto-refreshing dashboard with current statistics.
* Show thread status (RUNNING, PAUSED, STOPPED).
* Allow manual refresh and pause/resume.
* Use thread-safe collections (`ConcurrentHashMap`).
* Show active thread count and cache hit rate.

### US-6: Scheduled Automated Tasks
**As a system administrator, I want to schedule recurring tasks to run automatically.**

**Acceptance Criteria:**
* Use `ScheduledExecutorService`.
* Support daily, hourly, and weekly schedules.
* Implement tasks: Daily GPA recalculation, Hourly stats refresh, Weekly batch report, Daily backup.
* Display active schedules and next execution time.
* Persist schedules across restarts.
* Log all scheduled task executions.

### US-7: Advanced Pattern-Based Search
**As a teacher, I want to search using regex patterns so that I can find students matching complex criteria.**

**Acceptance Criteria:**
* Search by email domain (e.g., `@university.edu`).
* Search by phone area code or ID pattern with wildcards.
* Allow custom regex input.
* Display matched text highlighting.
* Show pattern match statistics and regex complexity hints.

### US-8: Thread-Safe Caching System
**As a developer, I want to implement a caching system so that frequently accessed data loads faster.**

**Acceptance Criteria:**
* Implement `ConcurrentHashMap` for thread-safe cache.
* Cache student objects, reports, and statistics.
* Implement LRU (Least Recently Used) eviction policy.
* Set max cache size (150 entries).
* Display hit/miss rates and memory usage.
* Implement background thread for refreshing stale entries.

### US-9: Concurrent Audit Trail
**As a system administrator, I want all operations logged with timestamps so that I can track system usage.**

**Acceptance Criteria:**
* Log all operations (add student, record grade, etc.).
* Use thread-safe logging (`ConcurrentLinkedQueue` or synchronized method).
* Include timestamp, thread ID, operation type, user action, and status.
* Write logs asynchronously to avoid blocking.
* Implement log rotation.

### US-10: Stream-Based Data Processing
**As a developer, I want to use Java Streams for data processing so that code is more readable and efficient.**

**Acceptance Criteria:**
* Use Stream API for filtering (`filter`), transforming (`map`), and aggregating (`reduce`).
* Use `collect()` for grouping/partitioning.
* Implement `parallelStream()` for large datasets.
* Process large CSV files using `Files.lines()`.
* Compare sequential vs. parallel stream performance.

---

## Architecture Requirements

### Collections Framework Design
* **Student Management:** `HashMap<String, Student>` (Primary store), `TreeMap` (GPA Rankings), `ArrayList` (Display order), `HashSet` (Unique courses).
* **Grade Management:** `LinkedList` (Grade history), `TreeMap` (Subject grades).
* **Concurrency:** `ConcurrentHashMap` (Stats cache), `PriorityQueue` (Task scheduling), `ConcurrentLinkedQueue` (Audit log).

### Thread Safety Requirements
* **Synchronization:** Synchronize modification of shared lists; use atomic variables (`AtomicInteger`, `AtomicLong`) for counters/timestamps.
* **File Ops:** Synchronized blocks or file locking for writes.
* **Resource Management:** `try-with-resources` for executors and file streams.

### Executor Framework
* **Fixed Thread Pool:** Batch report generation (CPU-bound).
* **Cached Thread Pool:** On-demand statistics (Short-lived).
* **Scheduled Thread Pool:** Recurring automated tasks.
* **Single Thread Executor:** Sequential audit log writing.

### NIO.2 File Operations
* **Path/Files:** Use for all I/O.
* **Streaming:** `Files.lines(path)` for memory efficiency.
* **Buffered Writing:** `Files.newBufferedWriter`.
* **Binary:** `ObjectOutputStream` for serialization.
* **WatchService:** Monitor imports directory.

---

## Testing Requirements
* **Unit Tests:** 25+ tests covering collections performance, concurrency safety, regex validation, and streams.
* **Integration Tests:** 10+ tests with mocking (ExecutorService, File System).
* **Performance Benchmarks:** Document collection access times, concurrent vs. sequential speeds, regex throughput, and file I/O efficiency.
* **Coverage:** 85%+ overall.

---

## Git Workflow Requirements
* **Branches:** `main`, `develop`, feature branches (e.g., `feature/concurrent-reports`, `feature/regex-validation`).
* **Commits:** 30+ meaningful commits using conventional format (e.g., `feat(nio2): add streaming CSV reader`).
* **Workflow:** Feature branch -> Pull Request (with screenshots/test results) -> Merge to Develop -> Tag Release.

---

## Minimum Requirements
* [ ] All 10 user stories implemented.
* [ ] All optimized collections implemented (`HashMap`, `TreeMap`, etc.).
* [ ] NIO.2 used for all file I/O (CSV, JSON, Binary).
* [ ] Regex validation for all inputs.
* [ ] Concurrency implemented (pools, real-time dashboard, safety verification).
* [ ] Stream processing used for filters/aggregations.
* [ ] 85%+ Test Coverage.
* [ ] Git workflow followed with 30+ commits.

---

## Grading Rubric

| Criteria | Points | What We're Looking For |
| :--- | :--- | :--- |
| **Collections Framework** | 15 | Appropriate types chosen, Big-O documented, efficient implementation. |
| **Concurrency & Threading** | 20 | Thread pools configured, no race conditions, proper sync, thread-safe collections. |
| **File I/O (NIO.2)** | 15 | NIO.2 API, large file streaming, multiple formats, resource cleanup. |
| **Regex Validation** | 10 | Patterns compiled once, comprehensive validation, clear error messages. |
| **Stream Processing** | 10 | Functional operations, parallel streams, memory-efficient processing. |
| **Testing** | 15 | 85%+ coverage, performance benchmarks, concurrency/regex tests. |
| **Git Workflow** | 10 | 30+ commits, feature branches, conventional messages, PRs documented. |
| **Code Quality** | 5 | Clean code, thread safety documented, JavaDoc, no duplication. |
| **Total** | **100** | |

---

## Testing Scenarios

1.  **Collections Performance:** Measure `HashMap` vs `ArrayList` lookup times with 100 and 500+ students. Verify O(1) behavior.
2.  **Concurrent Batch Reports:** Generate reports for 25 students using 6 threads. Verify speedup vs sequential and file integrity.
3.  **Real-Time Dashboard:** Update grades while dashboard runs. Verify auto-refresh every 5 seconds without data corruption.
4.  **Regex Validation:** Test valid/invalid inputs for ID, Email, Phone. Verify exact pattern matching.
5.  **Multi-Format File Ops:** Export/Import CSV, JSON, Binary. Verify data integrity and compare file sizes/generation times.
6.  **Scheduled Tasks:** Schedule a daily GPA recalculation. Verify it runs at the set time and updates values.
7.  **Pattern-Based Search:** Search by regex (e.g., email domain). Verify correct filtering and highlighting.
8.  **Stream Processing:** Process 10,000 records using parallel streams. Compare memory/speed against sequential loading.
9.  **Thread Safety:** Simultaneously add grades and generate reports. Verify no `ConcurrentModificationException` or lost data.
10. **Unit Tests:** Run full suite, check pass rate, and verify coverage report exceeds 85%.