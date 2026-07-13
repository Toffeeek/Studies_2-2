package application;

import application.db.DBConnection;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        statusLabel = createStatusLabel();

        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        loginButton.getStyleClass().add("primary-button");
        loginButton.setOnAction(event -> login(usernameField.getText(), passwordField.getText()));

        Button signupButton = new Button("Create Account");
        signupButton.getStyleClass().add("secondary-button");
        signupButton.setOnAction(event -> showSignupScreen());

        VBox card = createCard("Login", "Enter your account details");
        card.getChildren().addAll(
                createFormRow("Username", usernameField),
                createFormRow("Password", passwordField),
                createActions(loginButton, signupButton),
                statusLabel
        );

        setScene(card);
    }

    private void showSignupScreen() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose a password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");

        statusLabel = createStatusLabel();

        Button registerButton = new Button("Sign Up");
        registerButton.setDefaultButton(true);
        registerButton.getStyleClass().add("primary-button");
        registerButton.setOnAction(event -> signup(
                usernameField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText()
        ));

        Button backButton = new Button("Back to Login");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(event -> showLoginScreen());

        VBox card = createCard("Create Account", "Register a new user");
        card.getChildren().addAll(
                createFormRow("Username", usernameField),
                createFormRow("Password", passwordField),
                createFormRow("Confirm", confirmPasswordField),
                createActions(registerButton, backButton),
                statusLabel
        );

        setScene(card);
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

    private VBox createCard(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("title-label");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("subtitle-label");

        VBox card = new VBox(16, titleLabel, subtitleLabel);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("auth-card");
        return card;
    }

    private HBox createFormRow(String label, TextField field) {
        Label fieldLabel = new Label(label);
        fieldLabel.getStyleClass().add("field-label");

        field.setPrefWidth(300);

        HBox row = new HBox(14, fieldLabel, field);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox createActions(Button primaryButton, Button secondaryButton) {
        HBox actions = new HBox(12, secondaryButton, primaryButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        return actions;
    }

    private Label createStatusLabel() {
        Label label = new Label();
        label.getStyleClass().add("status-label");
        label.setWrapText(true);
        return label;
    }

    private void setScene(VBox card) {
        BorderPane root = new BorderPane(card);
        root.setPadding(new Insets(36));
        root.getStyleClass().add("root-pane");
        BorderPane.setAlignment(card, Pos.CENTER);

        Scene scene = new Scene(root, 620, 460);
        scene.getStylesheets().add(AuthApplication.class.getResource("/resources/styles.css").toExternalForm());
        stage.setScene(scene);
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
}
