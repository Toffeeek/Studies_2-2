package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaskList
{
    private final ObservableList<Task> tasks;

    public TaskList()
    {
        tasks = FXCollections.observableArrayList();
    }

    public ObservableList<Task> getTasks()
    {
        return tasks;
    }

    public void add(Task task)
    {
        tasks.add(task);
    }

    public void remove(Task task)
    {
        tasks.remove(task);
    }
}
