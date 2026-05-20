package org.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AllTasksController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> descColumn;
    @FXML private TableColumn<Task, String> deadlineColumn;
    @FXML private TableColumn<Task, Void> actionColumn;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    MainController.getTaskList().remove(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        taskTable.setItems(MainController.getTaskList().getTasks());
    }

    @FXML
    public void showAddTask(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/addTaskScreen.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setTitle("Task Manager - Add Task");
        stage.setScene(scene);
    }
}
