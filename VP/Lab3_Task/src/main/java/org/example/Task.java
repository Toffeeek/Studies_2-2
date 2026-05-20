package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task
{
    private final StringProperty name;
    private final StringProperty desc;
    private final StringProperty deadline;

    public Task(String name, String desc, String deadline)
    {
        this.name  = new SimpleStringProperty(name);
        this.desc    = new SimpleStringProperty(desc);
        this.deadline = new SimpleStringProperty(deadline);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDesc() {
        return desc.get();
    }

    public void setDesc(String desc) {
        this.desc.set(desc);
    }

    public StringProperty descProperty() {
        return desc;
    }

    public String getDeadline() {
        return deadline.get();
    }

    public void setDeadline(String deadline) {
        this.deadline.set(deadline);
    }

    public StringProperty deadlineProperty() {
        return deadline;
    }
}
