package interfaces;

import java.util.List;
import models.Student;

public interface Searchable {
    Student findStudent(String id);

    List<Student> searchByName(String name);
}
