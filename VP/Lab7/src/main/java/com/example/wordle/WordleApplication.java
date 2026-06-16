package com.example.wordle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WordleApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(WordleApplication.class.getResource("wordle-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("WORDLE");
        stage.setMinWidth(520);
        stage.setMinHeight(720);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
