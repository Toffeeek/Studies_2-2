package com.example.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/javafx_auth";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url(), username(), password());
    }

    public static void initialize() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGSERIAL PRIMARY KEY,
                    full_name VARCHAR(120) NOT NULL,
                    email VARCHAR(180) NOT NULL UNIQUE,
                    password VARCHAR(120) NOT NULL
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static String url() {
        return valueOrDefault("DB_URL", DEFAULT_URL);
    }

    private static String username() {
        return valueOrDefault("DB_USER", DEFAULT_USER);
    }

    private static String password() {
        return valueOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String valueOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
