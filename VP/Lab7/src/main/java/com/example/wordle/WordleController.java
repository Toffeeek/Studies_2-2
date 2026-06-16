package com.example.wordle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class WordleController {
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;
    private static final String[] KEY_ROWS = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};

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
        createBoard();
        createKeyboard();
        startNewGame();
        Platform.runLater(() -> {
            Node root = boardGrid.getScene().getRoot();
            root.addEventFilter(KeyEvent.KEY_PRESSED, this::handlePhysicalKey);
            root.requestFocus();
        });
    }

    private void createBoard() {
        boardGrid.getChildren().clear();
        for (int row = 0; row < MAX_ATTEMPTS; row++) {
            for (int col = 0; col < WORD_LENGTH; col++) {
                Label tile = new Label();
                tile.getStyleClass().addAll("tile", "tile-empty");
                tiles[row][col] = tile;
                boardGrid.add(tile, col, row);
            }
        }
    }

    private void createKeyboard() {
        addLetterRow(keyboardRowOne, KEY_ROWS[0]);
        addLetterRow(keyboardRowTwo, KEY_ROWS[1]);

        Button enter = createKey("ENTER");
        enter.getStyleClass().add("keyboard-key-wide");
        enter.setOnAction(event -> commitGuess());
        keyboardRowThree.getChildren().add(enter);

        addLetterRow(keyboardRowThree, KEY_ROWS[2]);

        Button delete = createKey("DEL");
        delete.getStyleClass().add("keyboard-key-wide");
        delete.setOnAction(event -> deleteLetter());
        keyboardRowThree.getChildren().add(delete);
    }

    private void addLetterRow(HBox rowBox, String letters) {
        for (char letter : letters.toCharArray()) {
            Button button = createKey(String.valueOf(letter));
            button.setOnAction(event -> addLetter(letter));
            keyboardButtons.put(letter, button);
            rowBox.getChildren().add(button);
        }
    }

    private Button createKey(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("keyboard-key");
        button.setFocusTraversable(false);
        return button;
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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(boardGrid.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(won ? "Congratulations" : "Out of tries");
        dialog.getDialogPane().getStyleClass().add("result-dialog");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        ButtonType playAgain = new ButtonType(won ? "Play Again" : "Try Again");
        dialog.getDialogPane().getButtonTypes().add(playAgain);
        dialog.getDialogPane().setContent(createDialogContent(won));
        dialog.setOnHidden(event -> startNewGame());
        dialog.showAndWait();
    }

    private VBox createDialogContent(boolean won) {
        Label heading = new Label(won ? "Congratulations!" : "Out of tries");
        heading.getStyleClass().add("result-heading");

        Label prefix = new Label(won ? "You found" : "The word was");
        prefix.getStyleClass().add("result-meaning");

        Label word = new Label(answer.word());
        word.getStyleClass().add("result-word");

        Label meaning = new Label(answer.meaning());
        meaning.setMaxWidth(260);
        meaning.getStyleClass().add("result-meaning");

        VBox content = new VBox(heading, prefix, word, meaning);
        content.setFillWidth(true);
        content.setMaxWidth(320);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.getStyleClass().add("result-content");
        return content;
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
