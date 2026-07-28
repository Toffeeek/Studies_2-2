package server;

import server.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class StorySeedDAO {
    public void initializeDatabase() throws SQLException {
        createTables();
        seedStories();
    }

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

    private void createTables() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS stories (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(80) NOT NULL UNIQUE,
                    display_name VARCHAR(120) NOT NULL,
                    avatar_url VARCHAR(500) NOT NULL,
                    image_url VARCHAR(500) NOT NULL,
                    own_story BOOLEAN NOT NULL DEFAULT FALSE,
                    unseen BOOLEAN NOT NULL DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private void seedStories() throws SQLException {
        String sql = """
                INSERT IGNORE INTO stories
                (username, display_name, avatar_url, image_url, own_story, unseen)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Object[] story : seedData()) {
                statement.setString(1, (String) story[0]);
                statement.setString(2, (String) story[1]);
                statement.setString(3, (String) story[2]);
                statement.setString(4, (String) story[3]);
                statement.setBoolean(5, (Boolean) story[4]);
                statement.setBoolean(6, (Boolean) story[5]);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private List<Object[]> seedData() {
        return List.of(
                story("you", "Your Story", "https://i.pravatar.cc/160?img=12", "https://picsum.photos/id/1025/860/1120", true, true),
                story("nora", "Nora", "https://i.pravatar.cc/160?img=47", "https://picsum.photos/id/1011/860/1120", false, true),
                story("samir", "Samir", "https://i.pravatar.cc/160?img=15", "https://picsum.photos/id/1015/860/1120", false, true),
                story("maliha", "Maliha", "https://i.pravatar.cc/160?img=32", "https://picsum.photos/id/1035/860/1120", false, false),
                story("arif", "Arif", "https://i.pravatar.cc/160?img=3", "https://picsum.photos/id/1062/860/1120", false, true),
                story("tania", "Tania", "https://i.pravatar.cc/160?img=44", "https://picsum.photos/id/1043/860/1120", false, false),
                story("ridwan", "Ridwan", "https://i.pravatar.cc/160?img=52", "https://picsum.photos/id/1041/860/1120", false, true)
        );
    }

    private Object[] story(String username, String displayName, String avatarUrl, String imageUrl, boolean ownStory, boolean unseen) {
        return new Object[]{username, displayName, avatarUrl, imageUrl, ownStory, unseen};
    }
}
