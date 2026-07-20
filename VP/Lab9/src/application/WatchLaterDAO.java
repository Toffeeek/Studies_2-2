package application;

import application.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WatchLaterDAO {
    public void addMovie(int movieId) throws SQLException {
        String sql = "INSERT IGNORE INTO watch_later (movie_id) VALUES (?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);
            statement.executeUpdate();
        }
    }

    public void removeMovie(int movieId) throws SQLException {
        String sql = "DELETE FROM watch_later WHERE movie_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);
            statement.executeUpdate();
        }
    }
}
