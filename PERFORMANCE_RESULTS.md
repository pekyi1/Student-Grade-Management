# Performance Analysis: HashMap vs ArrayList

## Overview
This document details the benchmarking results comparing the performance of `HashMap` (O(1)) versus `ArrayList` (O(n)) for student lookup operations within the `StudentManager`.

## Methodology
- **Benchmark Script**: `src/PerformanceTest.java`
- **Operation**: Lookup student by ID.
- **Scenario**: Worst-case search (finding the last element added).
- **Metric**: Execution time in nanoseconds (ns).

## Results

| Dataset Size | HashMap Lookup (ns) | ArrayList Search (ns) | Speedup Factor |
| :--- | :--- | :--- | :--- |
| **10,000** | 17,600 | 798,600 | **45x** |
| **100,000** | 17,900 | 2,551,300 | **142x** |
| **1,000,000** | 8,700 | 11,768,000 | **1352x** |

## Key Findings
1.  **Constant Time O(1)**: The `HashMap` implementation demonstrated consistent lookup times (~8-18 µs) regardless of the dataset size (from 10k to 1M records).
2.  **Linear Time O(n)**: The `ArrayList` implementation showed a linear increase in search time as the dataset grew.
3.  **Scalability**: For a dataset of 1 million students, the optimized `HashMap` lookup is over **1300 times faster** than the list iteration.

## Conclusion
The move to `HashMap` for student storage satisfies the requirement for O(1) access time and provides necessary scalability for large datasets.
