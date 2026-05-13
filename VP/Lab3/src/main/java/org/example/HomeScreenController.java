package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomeScreenController
{
    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private void loginHandler()
    {
        System.out.println("handler runs");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loginScreen.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login");
            stage.show();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void registerHandler()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/registerScreen.fxml"));
            Scene registerScene = new Scene(loader.load());

            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setTitle("Result");
            stage.show();
        }
        catch(Exception e)
        {

        }
    }



    @FXML
    public void initialize()
    {
        System.out.println("FXML Controller loaded!");
    }
}
