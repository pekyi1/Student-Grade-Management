package utils;

import java.io.*;

import java.util.List;

import java.util.stream.Collectors;

import models.Student;
import models.Grade;

/**
 * Utility class for serializing and deserializing data.
 * Supports manual JSON construction and Java Object Serialization.
 */
public class DataSerializer {

    // --- JSON Serialization ---

    /**
     * Serializes a Student object to a JSON string.
     * Uses manual string building to avoid external dependencies.
     */
    public static String toJson(Student student) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"id\": \"").append(escapeJson(student.getStudentId())).append("\",\n");
        json.append("  \"name\": \"").append(escapeJson(student.getName())).append("\",\n");
        json.append("  \"age\": ").append(student.getAge()).append(",\n");
        json.append("  \"email\": \"").append(escapeJson(student.getEmail())).append("\",\n");
        json.append("  \"phone\": \"").append(escapeJson(student.getPhone())).append("\",\n"); // Assuming getPhone()
                                                                                               // exists
        json.append("  \"type\": \"").append(escapeJson(student.getStudentType())).append("\"\n");
        json.append("}");
        return json.toString();
    }

    /**
     * Serializes a list of Grade objects to a JSON array string.
     */
    public static String toJson(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return "[]";
        }

        return "[\n" + grades.stream()
                .map(DataSerializer::gradeToJsonObject)
                .collect(Collectors.joining(",\n")) + "\n]";
    }

    private static String gradeToJsonObject(Grade grade) {
        StringBuilder json = new StringBuilder();
        json.append("  {\n");
        json.append("    \"studentId\": \"").append(escapeJson(grade.getStudentID())).append("\",\n");
        json.append("    \"subject\": \"").append(escapeJson(grade.getSubject().getSubjectName())).append("\",\n");
        json.append("    \"type\": \"").append(escapeJson(grade.getSubject().getSubjectType())).append("\",\n");
        json.append("    \"score\": ").append(grade.getGrade()).append("\n");
        json.append("  }");
        return json.toString();
    }

    // --- Binary Serialization ---

    /**
     * Serializes an object to a byte array.
     */
    public static byte[] serialize(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    /**
     * Deserializes an object from a byte array.
     */
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    // --- Helper Methods ---

    private static String escapeJson(String input) {
        if (input == null)
            return "";
        return input.replace("\"", "\\\"")
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
