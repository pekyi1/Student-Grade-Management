# Performance Benchmarks

## 1. Collection Access Times
Comparison of `HashMap` vs `ArrayList` for student ID lookups.

| Dataset Size | HashMap (ns) | ArrayList (ns) | Speedup |
| :--- | :--- | :--- | :--- |
| **10,000** | 17,600 | 798,600 | **45x** |
| **100,000** | 17,900 | 2,551,300 | **142x** |
| **1,000,000** | 8,700 | 11,768,000 | **1352x** |

**Conclusion:** HashMap provides consistent O(1) access times regardless of dataset size.

## 2. Concurrent vs. Sequential Processing
Measured throughput for Grade Calculation and Report Generation.

| Operation | Mode | Throughput / Time | Result |
| :--- | :--- | :--- | :--- |
| **GPA Calculation** | Sequential Stream | 30.6 ms | Baseline |
| **GPA Calculation** | Parallel Stream | 12.6 ms | **2.43x Speedup** |
| **Batch Reports** | Sequential Execution | ~6.5 reports/sec | Est. 15.3s total |
| **Batch Reports** | Concurrent (2 threads)| ~13 reports/sec | **7.78s total (2x Speedup)** |

## 3. File I/O Efficiency
Comparison of loading 10,000 records.

| Strategy | Performance | Memory Usage |
| :--- | :--- | :--- |
| **Stream (`Files.lines`)** | 55.26 ms | **Low (Constant)** |
| **List (`readAllLines`)** | 33.79 ms | High (Linear O(n)) |

**Note:** Streaming is preferred for stability with large files despite slight CPU overhead.

## 4. Regex Throughput
Performance of validation patterns during data seeding.

*   **Email Validation:** ~0.002ms per op
*   **Phone Validation:** ~0.001ms per op
*   **Pattern Compilation:** Done once (`static final`), preventing repeated overhead.
