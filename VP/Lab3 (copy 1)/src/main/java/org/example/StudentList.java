package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

public class StudentList
{
    @Getter
    private ObservableList<Student> students;
    public StudentList() {
        students = FXCollections.observableArrayList();
    }
    public void add(Student student) {
        students.add(student);
    }
}
