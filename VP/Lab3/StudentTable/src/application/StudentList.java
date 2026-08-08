package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
 
public class StudentList {
 
    private final ObservableList<Student> students;
 
    public StudentList() {
        students = FXCollections.observableArrayList();
    }
 
    public void add(Student student) {
        students.add(student);
    }
 
    public ObservableList<Student> getStudents() {
        return students;
    }
}
 