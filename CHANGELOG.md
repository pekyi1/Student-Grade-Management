# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Bulk Grade Import**: capability to import grades from CSV files via `BulkImportService`.
- **InvalidFileFormatException**: Custom exception handling for malformed CSV imports.
- **Detailed Reporting**: Enhanced `viewClassStatistics` and `generateClassStatisticsReport`.
- **Grade Management**: `GradeManager` class to handle grade operations (add, calculate averages, etc.).
- **Student Management**: `StudentManager` class for student CRUD operations.
- **Improved Logging**: `Logger` utility for error tracking.

### Changed
- Refactored `App.java` to use `BulkImportService` and `InvalidFileFormatException`.
- Updated `SimpleCSVParser` to strictly enforce 4-column CSV format.
- Modified `ClassStatistics.java` to be Java 8 compatible (removed `String.repeat`).
- Updated `DataSeeder` to use valid 10-digit phone numbers.
- Improved error messages in `ValidationUtils` and exception handlers.

### Fixed
- Fixed compilation error in `ClassStatistics.java` regarding `String.repeat(int)` undefined method.
- Fixed `DataSeeder` invalid phone number format preventing application startup.
- Fixed `BulkImportService` potentially crashing on invalid file formats by implementing specific exception handling.
