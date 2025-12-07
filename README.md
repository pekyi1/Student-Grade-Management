# Student Grade Management System

A robust, console-based Java application for managing student grades, calculating GPAs, and generating statistical reports. This system is designed with clean code principles, comprehensive exception handling, and a modular architecture.

## Features

- **Student Management**: Add, view, and search for students (Regular and Honors).
- **Grade Management**: Record grades, view reports, and calculate GPAs.
- **Bulk Import**: Import multiple grades via CSV files.
- **Statistics**: View class-wide performance analytics.
- **Reports**: Export detailed student grade reports to text files.
- **Robust Error Handling**: Custom exceptions for data integrity.

## getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher.
- Maven (for building and running tests).
- Git.

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/pekyi1/Student-Grade-Management.git
    cd Student-Grade-Management
    ```

2.  **Compile the project:**
    ```bash
    javac -d bin -sourcepath src src/App.java
    ```
    *Alternatively, using Maven:*
    ```bash
    mvn clean compile
    ```

3.  **Run the application:**
    ```bash
    java -cp bin App
    ```

## Testing

This project uses JUnit 4 for unit testing.

### Running Tests

To run all tests using Maven:
```bash
mvn test
```

To run a specific test class:
```bash
mvn test -Dtest=ClassStatisticsTest
```

### Manual Testing Scenarios

1.  **Bulk Import**: Place a CSV file in `imports/` and select option 7.
2.  **Export Report**: Select option 5, enter a valid Student ID, and check `reports/` folder.
3.  **Error Handling**: Try entering invalid data (e.g., age < 0, grade > 100) to verify error messages.

## Git Workflow

We follow a Feature Branch Workflow:

- **`main`**: Production-ready code.
- **`develop`**: Integration branch for ongoing development.
- **`feature/*`**: New features (e.g., `feature/bulk-import`).
- **`bugfix/*`**: Bug fixes (e.g., `bugfix/invalid-file-format`).

### Commit Guidelines

Use [Conventional Commits](https://www.conventionalcommits.org/):
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `test`: Adding or correcting tests
- `refactor`: Code change that neither fixes a bug nor adds a feature

Example:
```bash
git commit -m "feat: add InvalidFileFormatException for CSV parsing"
```

## License

[MIT](LICENSE)
