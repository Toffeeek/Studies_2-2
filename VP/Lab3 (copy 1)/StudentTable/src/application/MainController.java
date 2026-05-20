package application;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    // ── Input fields (fx:id must match exactly) ─────────────────────────────
    @FXML private TextField nameField;
    @FXML private TextField idField;
    @FXML private TextField emailField;

    // ── TableView and its columns ────────────────────────────────────────────
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> emailColumn;

    private final StudentList studentList = new StudentList();

    @FXML
    public void initialize() {
        // Tell each column which property of Student to display
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Bind the table to our ObservableList
        studentTable.setItems(studentList.getStudents());
    }

    @FXML
    public void handleAddStudent() {
        String name  = nameField.getText().trim();
        String id    = idField.getText().trim();
        String email = emailField.getText().trim();
        
        if (name.isEmpty() || id.isEmpty() || email.isEmpty()) {
            return;
        }

        Student student = new Student(name, id, email);
        studentList.add(student);

        // Clear the fields ready for the next entry
        nameField.clear();
        idField.clear();
        emailField.clear();
    }
}