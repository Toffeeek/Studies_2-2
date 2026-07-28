package server;

import java.sql.SQLException;

public class DatabaseServerApplication {
    public static void main(String[] args) {
        StorySeedDAO storySeedDAO = new StorySeedDAO();

        try {
            System.out.println("Starting Stories database setup...");
            storySeedDAO.initializeDatabase();
            System.out.println("Database is ready.");
            System.out.println("Stories available: " + storySeedDAO.countStories());
            System.out.println("You can now run the JavaFX client project.");
        } catch (SQLException exception) {
            System.err.println("Database setup failed.");
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }
}
