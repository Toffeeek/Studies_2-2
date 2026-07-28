package application;

import application.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoryDAO {
    public int countStories() throws SQLException {
        String sql = "SELECT COUNT(*) AS story_count FROM stories";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("story_count");
            }
            return 0;
        }
    }

    public List<Story> findStories() throws SQLException {
        String sql = """
                SELECT id, username, display_name, avatar_url, image_url, own_story, unseen
                FROM stories
                ORDER BY own_story DESC, created_at DESC, id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Story> stories = new ArrayList<>();
            while (resultSet.next()) {
                stories.add(toStory(resultSet));
            }
            return stories;
        }
    }

    public void resetAllStoriesToUnseen() throws SQLException {
        String sql = "UPDATE stories SET unseen = TRUE";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    public void deleteStory(int storyId) throws SQLException {
        String sql = "DELETE FROM stories WHERE id = ? AND own_story = TRUE";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, storyId);
            statement.executeUpdate();
        }
    }

    public void markSeen(int storyId) throws SQLException {
        String sql = "UPDATE stories SET unseen = FALSE WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, storyId);
            statement.executeUpdate();
        }
    }

    private Story toStory(ResultSet resultSet) throws SQLException {
        return new Story(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("display_name"),
                resultSet.getString("avatar_url"),
                resultSet.getString("image_url"),
                resultSet.getBoolean("own_story"),
                resultSet.getBoolean("unseen")
        );
    }
}
