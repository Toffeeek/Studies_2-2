package com.example.auth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLoginView();
        stage.setTitle("JavaFX Login Register");
        stage.setMinWidth(640);
        stage.setMinHeight(520);
        stage.show();
    }

    static void showLoginView() throws IOException {
        setScene(load("login-view.fxml"));
    }

    static void showSignupView() throws IOException {
        setScene(load("signup-view.fxml"));
    }

    private static Parent load(String resource) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(resource));
        return loader.load();
    }

    private static void setScene(Parent root) {
        Scene currentScene = primaryStage.getScene();
        if (currentScene == null) {
            primaryStage.setScene(new Scene(root, 720, 540));
        } else {
            currentScene.setRoot(root);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
