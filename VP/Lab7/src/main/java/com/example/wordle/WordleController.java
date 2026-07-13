package com.example.wordle;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordleController {
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;

    @FXML
    private GridPane boardGrid;
    @FXML
    private Label feedbackLabel;
    @FXML
    private HBox keyboardRowOne;
    @FXML
    private HBox keyboardRowTwo;
    @FXML
    private HBox keyboardRowThree;
    @FXML
    private VBox keyboardBox;

    private final WordRepository repository = new WordRepository();
    private final Label[][] tiles = new Label[MAX_ATTEMPTS][WORD_LENGTH];
    private final Map<Character, Button> keyboardButtons = new HashMap<>();
    private final Map<Character, LetterState> keyboardStates = new HashMap<>();

    private WordRepository.WordEntry answer;
    private int currentRow;
    private int currentColumn;
    private boolean gameOver;

    @FXML
    private void initialize() {
        wireBoardFromFxml();
        wireKeyboardFromFxml();
        startNewGame();
        Platform.runLater(() -> {
            Node root = boardGrid.getScene().getRoot();
            root.addEventFilter(KeyEvent.KEY_PRESSED, this::handlePhysicalKey);
            root.requestFocus();
        });
    }

    private void wireBoardFromFxml() {
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof Label tile) {
                Integer row = GridPane.getRowIndex(tile);
                Integer column = GridPane.getColumnIndex(tile);
                int rowIndex = row == null ? 0 : row;
                int columnIndex = column == null ? 0 : column;
                if (rowIndex < MAX_ATTEMPTS && columnIndex < WORD_LENGTH) {
                    tiles[rowIndex][columnIndex] = tile;
                }
            }
        }
    }

    private void wireKeyboardFromFxml() {
        registerKeyboardRow(keyboardRowOne);
        registerKeyboardRow(keyboardRowTwo);
        registerKeyboardRow(keyboardRowThree);
    }

    private void registerKeyboardRow(HBox rowBox) {
        for (Node node : rowBox.getChildren()) {
            if (node instanceof Button button && button.getText().length() == 1) {
                keyboardButtons.put(button.getText().charAt(0), button);
            }
        }
    }

    @FXML
    private void handleKeyboardButton(ActionEvent event) {
        if (!(event.getSource() instanceof Button button)) {
            return;
        }
        String value = button.getText();
        if ("ENTER".equals(value)) {
            commitGuess();
        } else if ("DEL".equals(value)) {
            deleteLetter();
        } else if (value.length() == 1) {
            addLetter(value.charAt(0));
        }
    }

    private void startNewGame() {
        answer = repository.randomAnswer();
        currentRow = 0;
        currentColumn = 0;
        gameOver = false;
        feedbackLabel.setText("");
        keyboardStates.clear();
        clearBoard();
        clearKeyboard();
        keyboardBox.setDisable(false);
    }

    private void clearBoard() {
        for (Label[] row : tiles) {
            for (Label tile : row) {
                tile.setText("");
                setTileState(tile, "tile-empty");
            }
        }
    }

    private void clearKeyboard() {
        for (Button button : keyboardButtons.values()) {
            button.getStyleClass().removeAll("key-correct", "key-present", "key-absent");
        }
    }

    private void handlePhysicalKey(KeyEvent event) {
        if (gameOver) {
            return;
        }
        KeyCode code = event.getCode();
        if (code == KeyCode.ENTER) {
            commitGuess();
            event.consume();
        } else if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
            deleteLetter();
            event.consume();
        } else if (code.isLetterKey() && code.getName().length() == 1) {
            addLetter(code.getName().charAt(0));
            event.consume();
        }
    }

    private void addLetter(char letter) {
        if (gameOver || currentColumn >= WORD_LENGTH) {
            return;
        }
        feedbackLabel.setText("");
        Label tile = tiles[currentRow][currentColumn];
        tile.setText(String.valueOf(Character.toUpperCase(letter)));
        setTileState(tile, "tile-filled");
        currentColumn++;
    }

    private void deleteLetter() {
        if (gameOver || currentColumn == 0) {
            return;
        }
        feedbackLabel.setText("");
        currentColumn--;
        Label tile = tiles[currentRow][currentColumn];
        tile.setText("");
        setTileState(tile, "tile-empty");
    }

    private void commitGuess() {
        if (gameOver) {
            return;
        }
        if (currentColumn < WORD_LENGTH) {
            feedbackLabel.setText("Not enough letters");
            return;
        }

        String guess = currentGuess();
        if (!repository.isValidGuess(guess)) {
            feedbackLabel.setText("Not in word list");
            return;
        }

        LetterState[] states = scoreGuess(guess, answer.word());
        revealGuess(guess, states);

        if (guess.equals(answer.word())) {
            finishGame(true);
        } else if (currentRow == MAX_ATTEMPTS - 1) {
            finishGame(false);
        } else {
            currentRow++;
            currentColumn = 0;
        }
    }

    private String currentGuess() {
        StringBuilder builder = new StringBuilder(WORD_LENGTH);
        for (int col = 0; col < WORD_LENGTH; col++) {
            builder.append(tiles[currentRow][col].getText());
        }
        return builder.toString();
    }

    private LetterState[] scoreGuess(String guess, String target) {
        LetterState[] states = new LetterState[WORD_LENGTH];
        Map<Character, Integer> remaining = new HashMap<>();

        for (int i = 0; i < WORD_LENGTH; i++) {
            char guessLetter = guess.charAt(i);
            char targetLetter = target.charAt(i);
            if (guessLetter == targetLetter) {
                states[i] = LetterState.CORRECT;
            } else {
                remaining.merge(targetLetter, 1, Integer::sum);
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (states[i] == LetterState.CORRECT) {
                continue;
            }
            char guessLetter = guess.charAt(i);
            int available = remaining.getOrDefault(guessLetter, 0);
            if (available > 0) {
                states[i] = LetterState.PRESENT;
                remaining.put(guessLetter, available - 1);
            } else {
                states[i] = LetterState.ABSENT;
            }
        }
        return states;
    }

    private void revealGuess(String guess, LetterState[] states) {
        for (int col = 0; col < WORD_LENGTH; col++) {
            LetterState state = states[col];
            setTileState(tiles[currentRow][col], state.tileStyleClass);
            updateKeyboardState(guess.charAt(col), state);
        }
    }

    private void updateKeyboardState(char letter, LetterState newState) {
        LetterState oldState = keyboardStates.get(letter);
        if (oldState != null && oldState.priority >= newState.priority) {
            return;
        }

        keyboardStates.put(letter, newState);
        Button button = keyboardButtons.get(letter);
        if (button != null) {
            button.getStyleClass().removeAll("key-correct", "key-present", "key-absent");
            button.getStyleClass().add(newState.keyStyleClass);
        }
    }

    private void finishGame(boolean won) {
        gameOver = true;
        keyboardBox.setDisable(true);
        showResultDialog(won);
    }

    private void showResultDialog(boolean won) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("result-dialog.fxml"));
            Parent content = loader.load();

            Stage dialog = new Stage(StageStyle.UNDECORATED);
            ResultDialogController controller = loader.getController();
            controller.setResult(won, answer.word(), answer.meaning(), currentRow + 1, () -> {
                dialog.close();
                startNewGame();
            });

            dialog.initOwner(boardGrid.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(won ? "Congratulations" : "Out of tries");

            Scene scene = new Scene(content);
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    dialog.close();
                    startNewGame();
                }
            });

            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.setOnCloseRequest(event -> startNewGame());
            dialog.showAndWait();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load result dialog.", exception);
        }
    }

    private void setTileState(Label tile, String stateClass) {
        tile.getStyleClass().removeAll("tile-empty", "tile-filled", "tile-correct", "tile-present", "tile-absent");
        tile.getStyleClass().add(stateClass);
    }

    private enum LetterState {
        ABSENT("tile-absent", "key-absent", 1),
        PRESENT("tile-present", "key-present", 2),
        CORRECT("tile-correct", "key-correct", 3);

        private final String tileStyleClass;
        private final String keyStyleClass;
        private final int priority;

        LetterState(String tileStyleClass, String keyStyleClass, int priority) {
            this.tileStyleClass = tileStyleClass;
            this.keyStyleClass = keyStyleClass;
            this.priority = priority;
        }
    }
}
