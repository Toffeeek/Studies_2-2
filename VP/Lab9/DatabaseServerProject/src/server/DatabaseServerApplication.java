package server;

import server.db.DBConnection;

import java.sql.SQLException;

public class DatabaseServerApplication {
    public static void main(String[] args) {
        MovieSeedDAO movieSeedDAO = new MovieSeedDAO();

        try {
            System.out.println("Starting Movie Scout database server setup...");
            System.out.println("Connecting to MySQL: " + DBConnection.getSettingsDescription());
            movieSeedDAO.initializeDatabase();
            int movieCount = movieSeedDAO.countMovies();
            System.out.println("Database is ready.");
            System.out.println("Movies available: " + movieCount);
            System.out.println("You can now run the JavaFX client project.");
        } catch (SQLException exception) {
            System.err.println("Database setup failed.");
            System.err.println(exception.getMessage());
            System.err.println();
            System.err.println("Current target: " + DBConnection.getSettingsDescription());
            System.err.println("Make sure MariaDB is running on 127.0.0.1:3306.");
            System.exit(1);
        }
    }
}
