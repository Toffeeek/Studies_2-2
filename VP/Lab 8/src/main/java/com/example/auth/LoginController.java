package com.example.auth;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button loginButton;

    private final UserDao userDao = new UserDao();

    @FXML
    private void initialize() {
        try {
            Database.initialize();
            statusLabel.setText("");
        } catch (SQLException exception) {
            statusLabel.setText("Database connection failed: " + exception.getMessage());
            loginButton.setDisable(true);
        }
    }

    @FXML
    private void handleLogin() {
        statusLabel.setText("");

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            statusLabel.setText("Email and password are required.");
            return;
        }

        try {
            boolean authenticated = userDao.authenticate(email, password);
            if (!authenticated) {
                statusLabel.setText("Invalid email or password.");
                return;
            }

            passwordField.clear();
            statusLabel.setText("Login successful.");
        } catch (SQLException exception) {
            statusLabel.setText("Login failed: " + exception.getMessage());
        }
    }

    @FXML
    private void handleOpenSignup() {
        try {
            Main.showSignupView();
        } catch (IOException exception) {
            statusLabel.setText("Could not open signup screen.");
        }
    }
}
