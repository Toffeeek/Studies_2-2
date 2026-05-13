package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultController {

    @FXML
    private Label resultLabel;

    public void setResult(int result) {
        resultLabel.setText(String.valueOf(result));
    }
}