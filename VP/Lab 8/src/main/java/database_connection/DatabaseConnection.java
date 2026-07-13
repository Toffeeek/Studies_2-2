package database_connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/vpl_lab";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "mysql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url(), username(), password());
    }

    public static void initializeDatabase() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS students (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(120) NOT NULL,
                    department VARCHAR(120) NOT NULL,
                    cgpa DOUBLE NOT NULL
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
