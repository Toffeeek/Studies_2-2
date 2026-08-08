package com.iut.chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public final class ChatClientApp extends Application {
    private final ObservableList<String> users = FXCollections.observableArrayList();
    private final Map<String, StringBuilder> conversations = new HashMap<>();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());

    private Stage stage;
    private Socket socket;
    private PrintWriter out;
    private String currentUser;
    private String selectedUser;

    private TextField usernameField;
    private PasswordField passwordField;
    private Label statusLabel;
    private ListView<String> userListView;
    private TextArea chatArea;
    private TextField messageField;
    private Label conversationTitle;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        connect();
        stage.setTitle("Lab 10 Chat");
        stage.setMinWidth(760);
        stage.setMinHeight(520);
        stage.setScene(new Scene(loginView(), 760, 520));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (socket != null) {
            socket.close();
        }
        super.stop();
    }

    private void connect() {
        try {
            socket = new Socket(ChatProtocol.HOST, ChatProtocol.PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            Thread listener = new Thread(this::listenToServer, "server-listener");
            listener.setDaemon(true);
            listener.start();
        } catch (IOException ex) {
            showError("Could not connect to the chat server. Run ChatServer first.");
        }
    }

    private Parent loginView() {
        Parent root = loadView("/login-view.fxml");
        usernameField = lookup(root, "usernameField");
        passwordField = lookup(root, "passwordField");
        statusLabel = lookup(root, "statusLabel");

        Button loginButton = lookup(root, "loginButton");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(event -> sendAuth("LOGIN"));

        Button registerButton = lookup(root, "registerButton");
        registerButton.setOnAction(event -> sendAuth("REGISTER"));
        return root;
    }

    private BorderPane chatView() {
        BorderPane root = loadView("/chat-view.fxml");
        userListView = lookup(root, "userListView");
        userListView.setItems(users);
        userListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.equals(currentUser) ? item + " (you)" : item);
                setDisable(item != null && item.equals(currentUser));
            }
        });
        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldUser, user) -> selectConversation(user));

        conversationTitle = lookup(root, "conversationTitle");
        chatArea = lookup(root, "chatArea");
        messageField = lookup(root, "messageField");
        messageField.setDisable(true);
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        Button sendButton = lookup(root, "sendButton");
        sendButton.setOnAction(event -> sendMessage());
        sendButton.disableProperty().bind(messageField.disabledProperty());
        return root;
    }

    private <T extends Parent> T loadView(String resourcePath) {
        URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing resource: " + resourcePath);
        }
        try {
            return FXMLLoader.load(resource);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load " + resourcePath, ex);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T lookup(Parent root, String id) {
        Object value = root.lookup("#" + id);
        if (value == null) {
            throw new IllegalStateException("Missing fx:id in FXML: " + id);
        }
        return (T) value;
    }

    private void sendAuth(String command) {
        if (out == null) {
            showError("Not connected to the server.");
            return;
        }
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        out.println(ChatProtocol.command(command, ChatProtocol.encode(username), ChatProtocol.encode(password)));
    }

    private void selectConversation(String user) {
        if (user == null || user.equals(currentUser)) {
            return;
        }
        selectedUser = user;
        conversationTitle.setText(user);
        messageField.setDisable(false);
        chatArea.setText(conversations.getOrDefault(user, new StringBuilder()).toString());
        out.println(ChatProtocol.command("HISTORY", ChatProtocol.encode(user)));
    }

    private void sendMessage() {
        if (selectedUser == null) {
            return;
        }
        String body = messageField.getText().trim();
        if (body.isBlank()) {
            return;
        }
        out.println(ChatProtocol.command("SEND", ChatProtocol.encode(selectedUser), ChatProtocol.encode(body)));
        messageField.clear();
    }

    private void listenToServer() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                String received = line;
                Platform.runLater(() -> handleServerLine(received));
            }
        } catch (IOException ex) {
            Platform.runLater(() -> showError("Disconnected from server."));
        }
    }

    private void handleServerLine(String line) {
        String[] parts = line.split("\\|", -1);
        switch (parts[0]) {
            case "REGISTER_OK" -> statusLabel.setText(ChatProtocol.decode(parts[1]));
            case "AUTH_OK" -> {
                currentUser = ChatProtocol.decode(parts[1]);
                stage.setTitle("Lab 10 Chat - " + currentUser);
                stage.setScene(new Scene(chatView(), 860, 560));
            }
            case "AUTH_FAIL", "ERROR" -> showError(ChatProtocol.decode(parts[1]));
            case "USERS" -> updateUsers(parts);
            case "HISTORY_BEGIN" -> conversations.put(ChatProtocol.decode(parts[1]), new StringBuilder());
            case "MESSAGE" -> appendMessage(parts);
            case "HISTORY_END" -> refreshConversation(ChatProtocol.decode(parts[1]));
            default -> {
            }
        }
    }

    private void updateUsers(String[] parts) {
        users.clear();
        for (int i = 1; i < parts.length; i++) {
            users.add(ChatProtocol.decode(parts[i]));
        }
    }

    private void appendMessage(String[] parts) {
        String sender = ChatProtocol.decode(parts[1]);
        String recipient = ChatProtocol.decode(parts[2]);
        long sentAt = Long.parseLong(parts[3]);
        String body = ChatProtocol.decode(parts[4]);
        String otherUser = sender.equals(currentUser) ? recipient : sender;

        StringBuilder builder = conversations.computeIfAbsent(otherUser, user -> new StringBuilder());
        builder.append('[')
                .append(timeFormatter.format(Instant.ofEpochMilli(sentAt)))
                .append("] ")
                .append(sender)
                .append(": ")
                .append(body)
                .append(System.lineSeparator());

        refreshConversation(otherUser);
    }

    private void refreshConversation(String user) {
        if (user.equals(selectedUser)) {
            chatArea.setText(conversations.getOrDefault(user, new StringBuilder()).toString());
            chatArea.positionCaret(chatArea.getText().length());
        }
    }

    private void showError(String message) {
        if (statusLabel != null
                && stage != null
                && stage.getScene() != null
                && stage.getScene().getRoot().lookup(".login-form") != null) {
            statusLabel.setText(message);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.show();
    }
}
