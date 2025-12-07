import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

// This class handles student search operations and result processing
public class StudentSearchService {

    // This method searches for a student by their exact ID
    public void searchById(Scanner scanner, StudentManager sm, GradeManager gm) {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        Student student = sm.findStudent(id);
        List<Student> results = new ArrayList<>();
        if (student != null) {
            results.add(student);
        }
        displaySearchResults(results, gm);
        if (!results.isEmpty()) {
            handleSearchActions(results, scanner, sm, gm);
        }
    }

    // This method searches for students by a partial name match
    public void searchByName(Scanner scanner, StudentManager sm, GradeManager gm) {
        System.out.print("Enter name (partial or full): ");
        String nameFragment = scanner.nextLine().trim().toLowerCase();

        List<Student> results = findStudentsByName(sm.getAllStudents(), nameFragment);

        displaySearchResults(results, gm);
        if (!results.isEmpty()) {
            handleSearchActions(results, scanner, sm, gm);
        }
    }

    public List<Student> findStudentsByName(Student[] allStudents, String nameFragment) {
        List<Student> results = new ArrayList<>();
        String search = nameFragment.toLowerCase();
        for (Student s : allStudents) {
            if (s.getName().toLowerCase().contains(search)) {
                results.add(s);
            }
        }
        return results;
    }

    // This method searches for students whose average grade falls within a range
    public void searchByGradeRange(Scanner scanner, StudentManager sm, GradeManager gm) {
        while (true) {
            try {
                double min = getDoubleInput(scanner, "Enter minimum grade: ");
                if (min < 0 || min > 100) {
                    throw new exceptions.InvalidGradeException(min);
                }

                double max = getDoubleInput(scanner, "Enter maximum grade: ");
                if (max < 0 || max > 100) {
                    throw new exceptions.InvalidGradeException(max);
                }

                if (min > max) {
                    System.out.println("Error: Minimum grade cannot be greater than maximum grade. Please try again.");
                    continue;
                }

                Student[] allStudents = sm.getAllStudents();
                List<Student> results = new ArrayList<>();
                for (Student s : allStudents) {
                    double avg = gm.calculateOverallAverage(s.getStudentId());
                    if (avg >= min && avg <= max) {
                        results.add(s);
                    }
                }
                displaySearchResults(results, gm);
                if (!results.isEmpty()) {
                    handleSearchActions(results, scanner, sm, gm);
                }
                break; // Exit loop after successful search
            } catch (exceptions.InvalidGradeException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
    }

    // This method filters students by their type (Regular or Honors)
    public void searchByType(Scanner scanner, StudentManager sm, GradeManager gm) {
        System.out.println("Select Student Type:");
        System.out.println("1. Regular");
        System.out.println("2. Honors");
        System.out.print("Select option (1-2): ");
        String choice = scanner.nextLine().trim();
        String type = choice.equals("1") ? "Regular" : (choice.equals("2") ? "Honors" : "");

        if (type.isEmpty()) {
            System.out.println("Invalid type selected.");
            return;
        }

        List<Student> results = findStudentsByType(sm.getAllStudents(), type);

        displaySearchResults(results, gm);
        if (!results.isEmpty()) {
            handleSearchActions(results, scanner, sm, gm);
        }
    }

    public List<Student> findStudentsByType(Student[] allStudents, String type) {
        List<Student> results = new ArrayList<>();
        for (Student s : allStudents) {
            if (s.getStudentType().equalsIgnoreCase(type)) {
                results.add(s);
            }
        }
        return results;
    }

    private void displaySearchResults(List<Student> results, GradeManager gm) {
        System.out.println("\nSEARCH RESULTS (" + results.size() + " found)");
        System.out.println("__________________________________________________");
        System.out.printf("%-8s | %-20s | %-10s | %s%n", "STU ID", "NAME", "TYPE", "AVG");
        System.out.println("__________________________________________________");

        if (results.isEmpty()) {
            System.out.println("No students found matching criteria.");
        } else {
            for (Student s : results) {
                double avg = gm.calculateOverallAverage(s.getStudentId());
                System.out.printf("%-8s | %-20s | %-10s | %.1f%%%n",
                        s.getStudentId(),
                        truncate(s.getName(), 20),
                        s.getStudentType(),
                        avg);
            }
        }
        System.out.println("__________________________________________________");
    }

    // This method processes user actions on search results (view details, export)
    private void handleSearchActions(List<Student> results, Scanner scanner, StudentManager sm, GradeManager gm) {
        System.out.println("\nActions:");
        System.out.println("1. View full details for a student");
        System.out.println("2. Export search results");
        System.out.println("3. New search");
        System.out.println("4. Return to main menu");
        System.out.print("\nEnter choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                while (true) {
                    System.out.print("Enter Student ID from list (or '0' to cancel): ");
                    String id = scanner.nextLine().trim();

                    if (id.equals("0")) {
                        break;
                    }

                    Student s = sm.findStudent(id);
                    if (s != null && results.contains(s)) {
                        gm.viewGradesByStudent(s);
                        break;
                    } else {
                        System.out.println("Student not found in search results. Please try again.");
                    }
                }
                break;
            case "2":
                exportSearchResults(results, scanner);
                break;
            case "3":
                break;
            case "4":
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // This method exports search results to a file
    private void exportSearchResults(List<Student> results, Scanner scanner) {
        System.out.print("Enter filename for export (without extension): ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty())
            filename = "search_results";

        try {
            FileExporter exporter = new FileExporter();
            String path = exporter.exportList(results, filename);
            System.out.println("Results exported to: " + path);
        } catch (java.io.IOException e) {
            System.out.println("Error exporting results: " + e.getMessage());
        }
    }

    private double getDoubleInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private String truncate(String str, int width) {
        if (str.length() > width) {
            return str.substring(0, width - 3) + "...";
        }
        return str;
    }
}
