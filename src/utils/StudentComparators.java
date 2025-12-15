package utils;

import models.Student;
import java.util.Comparator;

/**
 * Provides custom Comparators for sorting Student objects.
 * US-1: Implement custom Comparator for sorting students by multiple criteria.
 */
public class StudentComparators {

    /**
     * Sorts by Name (Alphabetical).
     */
    public static final Comparator<Student> BY_NAME = Comparator.comparing(Student::getName);

    /**
     * Sorts by Age (Youngest first).
     */
    public static final Comparator<Student> BY_AGE = Comparator.comparingInt(Student::getAge);

    /**
     * Sorts by ID.
     */
    public static final Comparator<Student> BY_ID = Comparator.comparing(Student::getStudentId);

    /**
     * Complex Comparator: Sorts by Type, then by Name.
     */
    public static final Comparator<Student> BY_TYPE_THEN_NAME = Comparator
            .comparing(Student::getStudentType)
            .thenComparing(Student::getName);

    /**
     * Sorts descending by ID (Newest first).
     */
    public static final Comparator<Student> BY_ID_DESC = BY_ID.reversed();
}
