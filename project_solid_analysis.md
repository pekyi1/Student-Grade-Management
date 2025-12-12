# SOLID Principles Analysis

This document explains how the Student Grade Management System conforms to the SOLID principles of Object-Oriented Design.

---

## 1. Single Responsibility Principle (SRP)
**Goal:** A class should have one, and only one, reason to change.

### Implementation in Code:
We separated our code into distinct packages based on responsibility:
*   **`models/Student.java`**: Responsible ONLY for holding data (name, ID, email). It doesn't know how to save itself or calculate class statistics.
*   **`services/StudentManager.java`**: Responsible ONLY for business logic (adding students, finding students). It doesn't handle UI logic.
*   **`App.java`**: Responsible ONLY for the User Interface (Console menu) and wiring components together.

### Simple Word Explanation:
> "We made sure each part of the code has just one job. The 'Student' class is like a form that just holds data. The 'Manager' is like a clerk who files that form. This way, if we want to change how we verify an email, we only change the Manager, not the form itself."

---

## 2. Open/Closed Principle (OCP)
**Goal:** Software entities should be open for extension, but closed for modification.

### Implementation in Code:
*   **`models/Subject.java`**: This is an abstract base class.
*   **`models/CoreSubject.java` & `models/ElectiveSubject.java`**: These extend `Subject`.
If we want to add a new subject type (e.g., `APSubject`), we just create a new class `APSubject`. We do **not** need to modify the original `Subject` class or break existing code.

### Simple Word Explanation:
> "We built the system so we can add new features without breaking old ones. It's like a power strip—you can plug in a new device (like a new Subject Type) without having to rewire the socket in the wall."

---

## 3. Liskov Substitution Principle (LSP)
**Goal:** You should be able to use a subclass (like `HonorsStudent`) wherever you use the parent class (`Student`) without breaking anything.

### Detailed Example 1: The Student Array
In `StudentManager.java`, we define our storage as an array of the parent type `Student`.
```java
// StudentManager.java - Lines 14, 45
private Student[] students; // Defined as generic 'Student'
students[studentCount++] = student; // Accepts ANY subclass
```
Because of LSP, we can freely mix different types in this one array:
```java
// DataSeeder.java - Lines 16-17
studentManager.addStudent(new RegularStudent(...)); // Works!
studentManager.addStudent(new HonorsStudent(...));  // Also works!
```
The `viewAllStudents` method loops through this array calling `displayStudentDetails()`. It doesn't need to know *which* type it is; it just works because both subclasses follow the rules of the parent.

### Detailed Example 2: The Subject Class
In `Grade.java`, we store a general `Subject`.
```java
// Grade.java - Line 12
private Subject subject; // Stores Core OR Elective
```
When we print the subject name:
```java
// Grade.java - Line 67
System.out.println("Subject: " + subject.getSubjectName());
```
This line works perfectly whether `subject` is actually a `CoreSubject` or an `ElectiveSubject`. We substituted the child for the parent, and the code didn't break.

---

## 4. Interface Segregation Principle (ISP)
**Goal:** Clients should not be forced to depend upon interfaces that they do not use.

### Implementation in Code:
Instead of one giant `UniversalInterface`, we have small, specific ones:
*   **`interfaces/Gradable`**: Defines `recordGrade`.
*   **`interfaces/Exportable`**: Defines `toExportFormat`.
*   **`interfaces/Searchable`**: Defines `matches`.
The `Grade` class implements `Gradable` and `Exportable` because it needs them. If we had a class that only needed to be searched but not graded (like a purely administrative record), it would only implement `Searchable`.

### Simple Word Explanation:
> "We prefer small, specific toolkits over one giant Swiss Army knife. If a class just needs to be printed, it shouldn't be forced to carry around the tools for calculating math. This keeps methods clean and relevant."

---

## 5. Dependency Inversion Principle (DIP)
**Goal:** Depend upon abstractions, not concretions.

### Implementation in Code:
*   **`services/BulkImportService.java`**:
    *   **Constructor**: `public BulkImportService(CSVParser csvParser)`
    *   It depends on the **interface** `CSVParser`, not the specific class `SimpleCSVParser`.
    *   This means we can swap out `SimpleCSVParser` with `FastCSVParser` or `JSONParser` in the future without changing a single line of code in the `BulkImportService`.

### Simple Word Explanation:
> "Our Import Service is designed like a remote control. It doesn't care what specific brand of battery (CSV Parser) is inside, as long as the battery fits the standard slot (Interface). This lets us swap out parts easily for testing or upgrades."
