package com.example.auth;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class SignupController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button signupButton;

    private final UserDao userDao = new UserDao();

    @FXML
    private void initialize() {
        try {
            Database.initialize();
            statusLabel.setText("");
        } catch (SQLException exception) {
            statusLabel.setText("Database connection failed: " + exception.getMessage());
            signupButton.setDisable(true);
        }
    }

    @FXML
    private void handleSignup() {
        statusLabel.setText("");

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            statusLabel.setText("All fields are required.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            statusLabel.setText("Enter a valid email address.");
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
            if (userDao.emailExists(email)) {
                statusLabel.setText("This email is already registered.");
                return;
            }

            userDao.createUser(name, email, password);
            clearFields();
            statusLabel.setText("Account created. Return to login.");
        } catch (SQLException exception) {
            statusLabel.setText("Registration failed: " + exception.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Main.showLoginView();
        } catch (IOException exception) {
            statusLabel.setText("Could not open login screen.");
        }
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
