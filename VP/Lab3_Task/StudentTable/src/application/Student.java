package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {

    private final StringProperty name;
    private final StringProperty id;
    private final StringProperty email;

    public Student(String name, String id, String email) {
        this.name  = new SimpleStringProperty(name);
        this.id    = new SimpleStringProperty(id);
        this.email = new SimpleStringProperty(email);
    }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getId() { return id.get(); }
    public void setId(String value) { id.set(value); }
    public StringProperty idProperty() { return id; }

    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }
}