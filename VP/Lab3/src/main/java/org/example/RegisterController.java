package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class RegisterController
{
    private static final ArrayList<User> users = new ArrayList<>();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize()
    {
        System.out.println("FXML Controller loaded!");
    }

    @FXML
    private void registerHandler()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = new User(username, password);
        users.add(user);

        System.out.println("Registered user: " + user.username);
        System.out.println("Total users: " + users.size());

        usernameField.clear();
        passwordField.clear();
    }

    private static class User
    {
        private final String username;
        private final String password;

        private User(String username, String password)
        {
            this.username = username;
            this.password = password;
        }
    }
}
