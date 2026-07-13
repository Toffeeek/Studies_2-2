package com.example.wordle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ResultDialogController {
    @FXML
    private Label headingLabel;
    @FXML
    private Label subheadingLabel;
    @FXML
    private Label wordLabel;
    @FXML
    private Label meaningLabel;
    @FXML
    private Button playAgainButton;

    private Runnable playAgainAction;

    public void setResult(boolean won, String answer, String meaning, int tries, Runnable playAgainAction) {
        this.playAgainAction = playAgainAction;
        headingLabel.setText(won ? "Congratulations!" : "Out of tries");
        subheadingLabel.setText(won ? "You found" : "The word was");
        wordLabel.setText(answer);
        meaningLabel.setText(meaning);
        playAgainButton.setText(won ? "Play Again" : "Try Again");
        playAgainButton.getStyleClass().removeAll("result-button-win", "result-button-loss");
        playAgainButton.getStyleClass().add(won ? "result-button-win" : "result-button-loss");
    }

    @FXML
    private void handlePlayAgain() {
        if (playAgainAction != null) {
            playAgainAction.run();
        }
    }
}
