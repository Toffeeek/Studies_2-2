package com.iut.chat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

final class ChatDatabase {
    private static final String DEFAULT_DB_URL = "jdbc:mariadb://localhost:3306/test";
    private static final String DEFAULT_DB_USER = "tawfiq";
    private static final String DEFAULT_DB_PASSWORD = "";
    private static final String DB_URL = setting("CHAT_DB_URL", DEFAULT_DB_URL);
    private static final String DB_USER = setting("CHAT_DB_USER", DEFAULT_DB_USER);
    private static final String DB_PASSWORD = setting("CHAT_DB_PASSWORD", DEFAULT_DB_PASSWORD);
    private final SecureRandom random = new SecureRandom();

    ChatDatabase() {
        initialize();
    }

    private void initialize() {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS lab10_chat_users (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        username VARCHAR(20) NOT NULL UNIQUE,
                        password_hash CHAR(64) NOT NULL,
                        salt CHAR(32) NOT NULL,
                        created_at BIGINT NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS lab10_chat_messages (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        sender VARCHAR(20) NOT NULL,
                        recipient VARCHAR(20) NOT NULL,
                        body TEXT NOT NULL,
                        sent_at BIGINT NOT NULL,
                        FOREIGN KEY (sender) REFERENCES lab10_chat_users(username),
                        FOREIGN KEY (recipient) REFERENCES lab10_chat_users(username),
                        INDEX idx_lab10_messages_pair (sender, recipient, sent_at)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                    """);
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not initialize chat database", ex);
        }
    }

    synchronized boolean register(String username, String password) {
        if (!isValidUsername(username) || password == null || password.length() < 4) {
            return false;
        }

        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String salt = HexFormat.of().formatHex(saltBytes);
        String hash = hashPassword(password, salt);

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO lab10_chat_users(username, password_hash, salt, created_at) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, hash);
            statement.setString(3, salt);
            statement.setLong(4, System.currentTimeMillis());
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    synchronized boolean authenticate(String username, String password) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT password_hash, salt FROM lab10_chat_users WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return false;
                }
                String expected = resultSet.getString("password_hash");
                String salt = resultSet.getString("salt");
                return expected.equals(hashPassword(password, salt));
            }
        } catch (SQLException ex) {
            return false;
        }
    }

    synchronized List<String> users() {
        List<String> users = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("SELECT username FROM lab10_chat_users ORDER BY username");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(resultSet.getString("username"));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not load users", ex);
        }
        return users;
    }

    synchronized Message saveMessage(String sender, String recipient, String body) {
        long sentAt = System.currentTimeMillis();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO lab10_chat_messages(sender, recipient, body, sent_at) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, sender);
            statement.setString(2, recipient);
            statement.setString(3, body);
            statement.setLong(4, sentAt);
            statement.executeUpdate();
            return new Message(sender, recipient, body, sentAt);
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not save message", ex);
        }
    }

    synchronized List<Message> history(String firstUser, String secondUser) {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT sender, recipient, body, sent_at
                     FROM lab10_chat_messages
                     WHERE (sender = ? AND recipient = ?)
                        OR (sender = ? AND recipient = ?)
                     ORDER BY sent_at
                     """)) {
            statement.setString(1, firstUser);
            statement.setString(2, secondUser);
            statement.setString(3, secondUser);
            statement.setString(4, firstUser);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    messages.add(new Message(
                            resultSet.getString("sender"),
                            resultSet.getString("recipient"),
                            resultSet.getString("body"),
                            resultSet.getLong("sent_at")));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not load message history", ex);
        }
        return messages;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static String setting(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private boolean isValidUsername(String username) {
        return username != null && username.matches("[A-Za-z0-9_]{3,20}");
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((salt + ":" + password).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }
}
