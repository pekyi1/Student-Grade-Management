# Stream Processing & Parallelism Performance Test Results
**Date:** 2025-12-15

## Test Configuration
- **Total Records:** 10,000
- **Data Source:** CSV File (`imports/large_grades_perf.csv`)
- **Metric:** Execution Time (ns/ms) and Memory Delta (bytes)

---

## 1. Import Method Comparison
We compared loading data using Java 8 Streams (`Files.lines`) versus loading all lines into memory (`Files.readAllLines`).

| Import Strategy | Execution Time | Memory Delta (Approx) | Characteristics |
|-----------------|----------------|-----------------------|-----------------|
| **Streaming**   | 55.26 ms       | ~1.06 MB              | Constant O(1) memory usage potential; slightly higher initial overhead. |
| **Load-All**    | 33.79 ms       | ~1.00 MB              | Faster for small/medium files; High O(n) memory risk for large datasets. |

**Analysis:**
For a dataset of this size (10k records), the overhead of checking stream processing prevents it from being faster than a raw load. However, the **Parallel Speedup** (see below) validates the use of streams for calculation.

---

## 2. Calculation Performance (Parallel vs. Sequential)
We measured the time to calculate the **average grade** across all 10,000 records using standard sequential streams versus parallel streams.

| Mode           | Execution Time (ns) | Speedup Factor | Result Verification |
|----------------|---------------------|----------------|---------------------|
| **Sequential** | 30,635,900 ns       | 1.0x           | Baseline            |
| **Parallel**   | 12,632,200 ns       | **2.43x**      | **Identical Result**|

### Conclusion
**Parallel Streams providing a ~2.4x speedup** indicates efficient utilization of multi-core architecture for aggregation tasks. This confirms that for CPU-intensive statistical operations (like calculating class averages or GPA distributions), parallel streams are highly effective.
