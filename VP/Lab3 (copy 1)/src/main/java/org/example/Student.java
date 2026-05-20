package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Student
{
    private final StringProperty name;
    private final StringProperty ID;
    private final StringProperty email;
    public Student(String name, String id, String email)
    {
        this.name  = new SimpleStringProperty(name);
        this.ID    = new SimpleStringProperty(id);
        this.email = new SimpleStringProperty(email);
    }
}