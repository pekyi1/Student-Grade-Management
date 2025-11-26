Here is the converted content in Markdown format.

# [cite_start]Student Grade Management System [cite: 3]

[cite_start]**Complexity:** Medium [cite: 4]
[cite_start]**Time Estimate:** 10 hours [cite: 5]

---

## Objectives
By completing this project, you will be able to:
* [cite_start]Apply OOP principles (encapsulation, inheritance, polymorphism) to design Java classes and interfaces for real-world problems[cite: 8].
* [cite_start]Create well-structured applications integrating primitive data types, control structures, and custom objects[cite: 9].
* [cite_start]Analyze class relationships to choose between inheritance, composition, abstract classes, and interfaces[cite: 10].
* [cite_start]Evaluate code quality using proper encapsulation, naming conventions, and OOP best practices[cite: 11].
* [cite_start]Apply polymorphic behavior with method overriding to build flexible, extensible code[cite: 12].

---

## Project Overview
[cite_start]You will build a console application for managing student grades with the following specifications[cite: 13, 14].

### Core Features
* [cite_start]**Add Student:** Register new students in the system[cite: 16].
* [cite_start]**View Students:** Display all students with their details[cite: 16].
* [cite_start]**Record Grade:** Add grades for students in different subjects[cite: 17].
* [cite_start]**View Grade Report:** Display grade history for a student[cite: 17].
* [cite_start]**Simple Menu:** Navigate through options[cite: 18].

### Student Types
* [cite_start]**Regular Student:** Standard grading (passing grade: 50%)[cite: 20].
* [cite_start]**Honors Student:** Higher standards (passing grade: 60%, eligible for honors recognition)[cite: 21].

### Subject Types
* [cite_start]**Core Subject:** Mandatory subjects (Mathematics, English, Science)[cite: 23].
* [cite_start]**Elective Subject:** Optional subjects (Music, Art, Physical Education)[cite: 24].

---

## User Stories & Technical Requirements

### [cite_start]US-1: View Students [cite: 330]
[cite_start]**As a teacher, I want to view all students so that I can see their details and performance [cite: 331-333].**

**Acceptance Criteria:**
* [cite_start]Display minimum 5 students (3 Regular, 2 Honors)[cite: 335].
* [cite_start]Show student ID, name, type, average grade, and status[cite: 336].
* [cite_start]Regular students show passing grade of 50%[cite: 337].
* [cite_start]Honors students show passing grade of 60% and honors eligibility[cite: 338].
* [cite_start]Display total students and average class grade[cite: 339].

**Classes to Create:**

1.  [cite_start]**Student (Abstract Class)** [cite: 341]
    * [cite_start]**Fields:** `studentId` (String), `name` (String), `age` (int), `email` (String), `phone` (String), `status` (String), and static `studentCounter` (int)[cite: 342].
    * [cite_start]**Methods:** Constructor, getters, setters[cite: 343].
        * [cite_start]`abstract displayStudentDetails()`[cite: 344].
        * [cite_start]`abstract getStudentType()`[cite: 345].
        * [cite_start]`abstract getPassingGrade()`[cite: 346].
        * [cite_start]`calculateAverageGrade()`[cite: 347].
        * [cite_start]`isPassing()`[cite: 348].

2.  [cite_start]**RegularStudent (Extends Student)** [cite: 349]
    * [cite_start]**Field:** `passingGrade` (double) set to 50.0[cite: 350].
    * **Methods:**
        * [cite_start]Constructor accepting details[cite: 351].
        * [cite_start]Override `displayStudentDetails()`[cite: 352].
        * [cite_start]Override `getStudentType()` to return "Regular"[cite: 353].
        * [cite_start]Override `getPassingGrade()` to return 50.0[cite: 354].

3.  [cite_start]**HonorsStudent (Extends Student)** [cite: 355]
    * [cite_start]**Fields:** `passingGrade` (double) set to 60.0, `honorsEligible` (boolean)[cite: 356].
    * **Methods:**
        * [cite_start]Constructor accepting details[cite: 356].
        * [cite_start]Override `displayStudentDetails()` (include honors status)[cite: 357].
        * [cite_start]Override `getStudentType()` to return "Honors"[cite: 358].
        * [cite_start]Override `getPassingGrade()` to return 60.0[cite: 359].
        * [cite_start]`checkHonorsEligibility()` returns true if average $\ge 85\%$[cite: 360, 361].

---

### [cite_start]US-2: Add Student [cite: 362]
[cite_start]**As a teacher, I want to add new students so that I can track their grades [cite: 363-365].**

**Acceptance Criteria:**
* [cite_start]Capture student details (name, age, email, phone)[cite: 367].
* [cite_start]Support two student types: Regular and Honors[cite: 368].
* [cite_start]Auto-generate unique student ID[cite: 369].
* [cite_start]Display confirmation with all details[cite: 370].
* [cite_start]Set initial status as "Active"[cite: 371].

**Classes to Create:**
* [cite_start]No new classes needed; uses Student hierarchy from US-1[cite: 373].

---

### [cite_start]US-3: Record Grade [cite: 374]
[cite_start]**As a teacher, I want to record grades for students so that I can track their performance [cite: 375-377].**

**Acceptance Criteria:**
* [cite_start]User enters student ID (validate existence)[cite: 379, 380].
* [cite_start]Allow selection of subject type (Core/Elective) and specific subject[cite: 381, 382].
* [cite_start]Enter grade (0-100) and validate range[cite: 383, 384].
* [cite_start]Generate unique grade ID[cite: 385].
* [cite_start]Show confirmation before finalizing[cite: 386].

**Classes to Create:**

1.  [cite_start]**Subject (Abstract Class)** [cite: 388]
    * [cite_start]**Fields:** `subjectName` (String), `subjectCode` (String)[cite: 389].
    * [cite_start]**Methods:** Constructor, getters, setters, `abstract displaySubjectDetails()`, `abstract getSubjectType()` [cite: 390-392].

2.  [cite_start]**CoreSubject (Extends Subject)** [cite: 393]
    * [cite_start]**Field:** `mandatory` (boolean) always true[cite: 394].
    * [cite_start]**Methods:** Override details and type ("Core"), `isMandatory()` returns true [cite: 396-398].

3.  [cite_start]**ElectiveSubject (Extends Subject)** [cite: 399]
    * [cite_start]**Field:** `mandatory` (boolean) always false[cite: 400].
    * [cite_start]**Methods:** Override details and type ("Elective"), `isMandatory()` returns false [cite: 402-404].

4.  [cite_start]**Gradable (Interface)** [cite: 405]
    * [cite_start]`recordGrade(double grade)`[cite: 407].
    * [cite_start]`validateGrade(double grade)` returns boolean[cite: 407].

5.  [cite_start]**Grade** [cite: 408]
    * [cite_start]**Fields:** Static `gradeCounter` (int), `gradeId` (String), `studentId` (String), `subject` (Subject), `grade` (double), `date` (String)[cite: 409, 410].
    * [cite_start]**Methods:** Auto-generate ID and date, getters, `displayGradeDetails()`, `getLetterGrade()` [cite: 411-416].

---

### [cite_start]US-4: View Grade Report [cite: 417]
[cite_start]**As a teacher, I want to view grade report for a student so that I can track their progress [cite: 418-420].**

**Acceptance Criteria:**
* [cite_start]Display all grades for a specific student in reverse chronological order[cite: 422, 427].
* [cite_start]Show grade ID, date, subject, type, and grade[cite: 423].
* [cite_start]Calculate and display averages for core subjects, elective subjects, and overall[cite: 424].
* [cite_start]Show performance summary (passing status)[cite: 425].
* [cite_start]Handle students with no grades[cite: 426].

**Classes to Create:**

1.  [cite_start]**GradeManager (Uses Composition)** [cite: 429]
    * [cite_start]**Fields:** `grades` (Grade array, size 200), `gradeCount` (int)[cite: 430, 431].
    * **Methods:**
        * [cite_start]`addGrade(Grade)`[cite: 433].
        * [cite_start]`viewGradesByStudent(String studentId)`[cite: 433].
        * [cite_start]`calculateCoreAverage(String studentId)`[cite: 435].
        * [cite_start]`calculateElectiveAverage(String studentId)`[cite: 435].
        * [cite_start]`calculateOverallAverage(String studentId)`[cite: 435].
        * [cite_start]`getGradeCount()`[cite: 435].

---

### [cite_start]US-5: Simple Menu Navigation [cite: 436]
[cite_start]**As a user, I want to navigate through menu options so that I can use all features [cite: 437-439].**

**Acceptance Criteria:**
* [cite_start]Display clear menu with 5 options[cite: 441].
* [cite_start]Accept and validate user input[cite: 442].
* [cite_start]Execute selected option and loop until exit[cite: 443, 444].
* [cite_start]Handle invalid input gracefully[cite: 445].

**Classes to Create:**

1.  [cite_start]**StudentManager (Uses Composition)** [cite: 447]
    * [cite_start]**Fields:** `students` (Student array, size 50), `studentCount` (int)[cite: 448, 449].
    * **Methods:**
        * [cite_start]`addStudent(Student)`[cite: 451].
        * [cite_start]`findStudent(String studentId)`[cite: 452].
        * [cite_start]`viewAllStudents()`[cite: 453].
        * [cite_start]`getAverageClassGrade()`[cite: 454].
        * [cite_start]`getStudentCount()`[cite: 455].

---

## Minimum Requirements

### Implementation Checklist
* [cite_start][ ] All 9 required classes implemented[cite: 458].
* [cite_start][ ] All 5 user stories working[cite: 459].
* [cite_start][ ] Static counters work correctly for ID generation[cite: 460].
* [cite_start][ ] Use arrays to manage and retrieve students and grades[cite: 461].
* [cite_start][ ] Application runs without errors[cite: 462].
* [cite_start][ ] Input validation implemented[cite: 463].
* [cite_start][ ] Grade history tracks all operations[cite: 464].

### Required Classes (9 Total)
1.  [cite_start]Student (abstract) [cite: 466]
2.  [cite_start]RegularStudent [cite: 466]
3.  [cite_start]HonorsStudent [cite: 466]
4.  [cite_start]Subject (abstract) [cite: 467]
5.  [cite_start]CoreSubject [cite: 467]
6.  [cite_start]ElectiveSubject [cite: 467]
7.  [cite_start]Grade [cite: 469]
8.  [cite_start]StudentManager [cite: 469]
9.  [cite_start]GradeManager [cite: 469]
* [cite_start]Interface: Gradable [cite: 468]
* [cite_start]Main Class [cite: 469]

### OOP Principles
* [cite_start]Private fields with public getters/setters[cite: 477].
* [cite_start]Inheritance (Student & Subject hierarchies)[cite: 478].
* [cite_start]Abstract classes and methods[cite: 479].
* [cite_start]Interface implementation (Gradable)[cite: 480].
* [cite_start]Method overriding[cite: 481].
* [cite_start]Polymorphism[cite: 482].
* [cite_start]Composition (Managers holding arrays)[cite: 483].
* [cite_start]Static members (ID counters)[cite: 483].

---

## Grading Rubric

| Criteria | Points | What We're Looking For |
| :--- | :--- | :--- |
| **OOP Principles** | 25 | [cite_start]Encapsulation (private fields), inheritance (2 hierarchies), polymorphism (method overriding), abstraction (abstract classes + interface), composition (Manager classes)[cite: 491]. |
| **Functionality** | 25 | [cite_start]All 5 user stories work: view students, add students, record grades, view reports, menu navigation[cite: 492]. |
| **Class Design** | 15 | [cite_start]All 9 required classes created, proper relationships, appropriate use of abstract classes and interfaces, correct use of static fields for ID generation[cite: 492]. |
| **Data Management** | 15 | [cite_start]Proper use of arrays for student and grade management, correct application of search and iteration, code efficiency and clarity[cite: 492]. |
| **Code Quality** | 10 | [cite_start]Clean code, proper naming, good formatting, input validation, no errors[cite: 492]. |
| **Documentation** | 10 | [cite_start]README with setup instructions, code comments for complex logic, clear user prompts[cite: 492]. |
| **Total** | [cite_start]**100** | [cite: 492] |

---

## Testing Scenarios

1.  [cite_start]**View Students:** Run app, select option 2, verify 5 students display (Regular/Honors distinction), and check statistics [cite: 494-500].
2.  [cite_start]**Add Regular Student:** Select option 1, enter details, select Regular, verify ID generation and 50% passing grade [cite: 501-507].
3.  [cite_start]**Add Honors Student:** Select option 1, enter details, select Honors, verify honors eligibility and 60% passing grade [cite: 508-513].
4.  [cite_start]**Record Grade (Core):** Select option 3, enter valid ID, select Core, enter grade, verify grade ID generation and confirmation [cite: 514-522].
5.  [cite_start]**Record Grade (Elective):** Select option 3, enter valid ID, select Elective, enter grade, verify transaction success [cite: 523-529].
6.  **Grade Validation:** Try to enter grades < 0 or > 100 and verify system rejection. [cite_start]Verify valid grades are accepted [cite: 530-538].
7.  [cite_start]**View Grade Report (Empty):** View report for a new student and verify "No grades recorded" message [cite: 539-543].
8.  [cite_start]**View Grade Report (With Records):** Record 3-5 grades, view report, verify chronological order, averages, and passing status [cite: 544-550].
9.  **Honors Eligibility Check:** Add Honors student, record grades $\ge 85\%$, check eligibility status. [cite_start]Record lower grades and verify status change [cite: 551-558].
10. **Unit Tests Execution:** Run JUnit tests (aim for 85% coverage) and integration tests. [cite_start]Document failures [cite: 559-569].

### Submission
[cite_start]Submit your project here: Tally[cite: 571].