# Collection Complexity Analysis

This document details the Big-O time complexity for the core data structures used in the Student Grade Management System.

## 1. Student Storage: `HashMap<String, Student>`
Used in `StudentManager` to map Student IDs to Student objects.

| Operation | Complexity | Explanation |
| :--- | :--- | :--- |
| **Insertion** (`put`) | **O(1)** | Hashing the key provides direct access to the bucket. |
| **Lookup** (`get`) | **O(1)** | Retrieval by ID is constant time, essential for system performance. |
| **Deletion** (`remove`) | **O(1)** | Removing by ID is constant time. |
| **Iteration** (`values()`) | **O(n)** | Processing all students requires visiting every bucket. |

## 2. Grade History: `LinkedList<Grade>`
Used in `GradeManager` to store the complete history of all grades.

| Operation | Complexity | Explanation |
| :--- | :--- | :--- |
| **Append** (`add`) | **O(1)** | Adding to the tail of a doubly-linked list is constant time. |
| **filter by Student** | **O(n)** | Finding grades for a specific student requires traversing the list. |
| **Iteration** | **O(n)** | Standard traversal. |

## 3. Class Rankings: `TreeMap<Double, List<Student>>`
Used for generating reports sorted by GPA.

| Operation | Complexity | Explanation |
| :--- | :--- | :--- |
| **Insertion** | **O(log n)** | Red-Black tree maintenance. |
| **Traversal** | **O(n)** | In-order traversal yields sorted data. |

## 4. Unique Courses: `HashSet<Subject>`
Used to track the set of unique subjects.

| Operation | Complexity | Explanation |
| :--- | :--- | :--- |
| **Add** | **O(1)** | Hash-based set ensures uniqueness with constant time checks. |
| **Contains** | **O(1)** | Verifying existence is constant. |

## 5. Statistical Cache: `LinkedHashMap` (LRU)
Used in `CacheService` to cache expensive calculations like GPA.

| Operation | Complexity | Explanation |
| :--- | :--- | :--- |
| **Access** | **O(1)** | Hash map lookup combined with O(1) list reordering for LRU. |
| **Eviction** | **O(1)** | Removing the eldest entry is constant time. |
