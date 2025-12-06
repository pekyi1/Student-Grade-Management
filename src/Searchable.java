import java.util.List;

public interface Searchable {
    Student findStudent(String id);

    List<Student> searchByName(String name);
}
