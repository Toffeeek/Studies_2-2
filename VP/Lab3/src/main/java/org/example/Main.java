package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
//        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/firstScreen.fxml"));
//        Scene scene = new Scene(loader.load());
//
//        stage.setTitle("Adder First Scene");
//        stage.setScene(scene);
//        stage.show();


        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/homeScreen.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Home Screen");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}