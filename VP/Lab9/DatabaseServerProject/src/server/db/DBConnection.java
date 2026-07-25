package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DATABASE = "vpl_lab";
    private static final String SERVER_URL = "jdbc:mysql://127.0.0.1:3306"
            + "?connectTimeout=5000&socketTimeout=5000";
    private static final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/" + DATABASE
            + "?connectTimeout=5000&socketTimeout=5000";
    private static final String USER = "root";
    private static final String PASSWORD = "mysql";

    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
    }

    public static String getSettingsDescription() {
        return USER + "@127.0.0.1:3306/" + DATABASE;
    }

    public static String getDatabaseName() {
        return DATABASE;
    }
}
