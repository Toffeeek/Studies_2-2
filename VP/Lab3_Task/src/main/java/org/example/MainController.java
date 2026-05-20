package org.example;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController {

    @FXML private TextField nameField;
    @FXML private TextArea descField;
    @FXML private DatePicker deadlinePicker;

    private static final TaskList taskList = new TaskList();
    private static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @FXML
    public void handleAddTask() {
        String name  = nameField.getText().trim();
        String desc = descField.getText().trim();

        if (name.isEmpty() || desc.isEmpty() || deadlinePicker.getValue() == null) {
            return;
        }

        taskList.add(new Task(name, desc, deadlinePicker.getValue().format(DEADLINE_FORMATTER)));

        nameField.clear();
        descField.clear();
        deadlinePicker.setValue(null);
    }

    @FXML
    public void showAllTasks(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/allTasksScreen.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setTitle("All Tasks");
        stage.setScene(scene);
    }

    public static TaskList getTaskList() {
        return taskList;
    }
}
