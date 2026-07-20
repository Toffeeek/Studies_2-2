package application;

import application.db.DBConnection;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class AuthApplication extends Application {
    private final UserDAO userDAO = new UserDAO();

    private Stage stage;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        initializeDatabase();

        stage.setTitle("Login and Registration");
        stage.setMinWidth(620);
        stage.setMinHeight(460);
        showLoginScreen();
        stage.show();
    }

    private void initializeDatabase() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(80) NOT NULL UNIQUE,
                    password VARCHAR(120) NOT NULL
                )
                """;

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            showFatalScreen("Database connection failed: " + exception.getMessage());
        }
    }

    private void showLoginScreen() {
        LoadedView view = loadView("/resources/login-view.fxml");
        Map<String, Object> controls = view.controls();

        TextField usernameField = getControl(controls, "usernameField", TextField.class);
        PasswordField passwordField = getControl(controls, "passwordField", PasswordField.class);
        Button loginButton = getControl(controls, "loginButton", Button.class);
        Button signupButton = getControl(controls, "signupButton", Button.class);
        statusLabel = getControl(controls, "statusLabel", Label.class);

        loginButton.setOnAction(event -> login(usernameField.getText(), passwordField.getText()));
        signupButton.setOnAction(event -> showSignupScreen());

        setScene(view.root());
    }

    private void showSignupScreen() {
        LoadedView view = loadView("/resources/signup-view.fxml");
        Map<String, Object> controls = view.controls();

        TextField usernameField = getControl(controls, "usernameField", TextField.class);
        PasswordField passwordField = getControl(controls, "passwordField", PasswordField.class);
        PasswordField confirmPasswordField = getControl(controls, "confirmPasswordField", PasswordField.class);
        Button registerButton = getControl(controls, "registerButton", Button.class);
        Button backButton = getControl(controls, "backButton", Button.class);
        statusLabel = getControl(controls, "statusLabel", Label.class);

        registerButton.setOnAction(event -> signup(
                usernameField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText()
        ));
        backButton.setOnAction(event -> showLoginScreen());

        setScene(view.root());
    }

    private void login(String username, String password) {
        String cleanUsername = username.trim();
        statusLabel.setText("");

        if (cleanUsername.isBlank() || password.isBlank()) {
            statusLabel.setText("Username and password are required.");
            return;
        }

        try {
            if (userDAO.authenticate(cleanUsername, password)) {
                statusLabel.getStyleClass().setAll("success-label");
                statusLabel.setText("Login successful.");
            } else {
                statusLabel.setText("Invalid username or password.");
            }
        } catch (SQLException exception) {
            statusLabel.setText("Login failed: " + exception.getMessage());
        }
    }

    private void signup(String username, String password, String confirmPassword) {
        String cleanUsername = username.trim();
        statusLabel.setText("");

        if (cleanUsername.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            statusLabel.setText("All fields are required.");
            return;
        }

        if (cleanUsername.length() < 3) {
            statusLabel.setText("Username must be at least 3 characters.");
            return;
        }

        if (password.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        try {
            if (userDAO.usernameExists(cleanUsername)) {
                statusLabel.setText("This username already exists.");
                return;
            }

            userDAO.createUser(cleanUsername, password);
            statusLabel.getStyleClass().setAll("success-label");
            statusLabel.setText("Account created. Back to login to continue.");
        } catch (SQLException exception) {
            statusLabel.setText("Signup failed: " + exception.getMessage());
        }
    }

    private void setScene(Parent root) {
        Scene scene = new Scene(root, 620, 460);
        scene.getStylesheets().add(AuthApplication.class.getResource("/resources/styles.css").toExternalForm());
        stage.setScene(scene);
    }

    private LoadedView loadView(String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(AuthApplication.class.getResource(resourcePath));
            Parent root = loader.load();
            return new LoadedView(root, loader.getNamespace());
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load " + resourcePath, exception);
        }
    }

    private <T> T getControl(Map<String, Object> controls, String id, Class<T> type) {
        return type.cast(controls.get(id));
    }

    private void showFatalScreen(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("status-label");
        label.setWrapText(true);

        BorderPane root = new BorderPane(label);
        root.setPadding(new Insets(32));

        Scene scene = new Scene(root, 620, 320);
        stage.setScene(scene);
    }

    private record LoadedView(Parent root, Map<String, Object> controls) {
    }
}
