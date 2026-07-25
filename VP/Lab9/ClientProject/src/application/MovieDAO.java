package application;

import application.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    public int countMovies() throws SQLException {
        String sql = "SELECT COUNT(*) AS movie_count FROM movies";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("movie_count");
            }
            return 0;
        }
    }

    public List<Movie> findMovies(String searchText, String genre, boolean watchLaterOnly) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT m.id, m.title, m.genres, m.cast_members, m.duration_minutes,
                       m.imdb_rating, m.summary, m.poster_url,
                       CASE WHEN wl.movie_id IS NULL THEN FALSE ELSE TRUE END AS watch_later
                FROM movies m
                LEFT JOIN watch_later wl ON wl.movie_id = m.id
                WHERE LOWER(m.title) LIKE LOWER(?)
                """);

        List<Object> values = new ArrayList<>();
        values.add("%" + searchText.trim() + "%");

        if (genre != null && !genre.equals("All Genres")) {
            sql.append(" AND LOWER(m.genres) LIKE LOWER(?)");
            values.add("%" + genre + "%");
        }

        if (watchLaterOnly) {
            sql.append(" AND wl.movie_id IS NOT NULL");
        }

        sql.append(" ORDER BY m.imdb_rating DESC, m.title");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            bindValues(statement, values);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Movie> movies = new ArrayList<>();
                while (resultSet.next()) {
                    movies.add(toMovie(resultSet));
                }
                return movies;
            }
        }
    }

    public List<String> findGenres() throws SQLException {
        String sql = "SELECT genres FROM movies ORDER BY genres";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<String> genres = new ArrayList<>();
            while (resultSet.next()) {
                for (String genre : resultSet.getString("genres").split(",")) {
                    String cleanGenre = genre.trim();
                    if (!cleanGenre.isBlank() && !genres.contains(cleanGenre)) {
                        genres.add(cleanGenre);
                    }
                }
            }
            genres.sort(String::compareToIgnoreCase);
            return genres;
        }
    }

    public Movie findById(int movieId) throws SQLException {
        String sql = """
                SELECT m.id, m.title, m.genres, m.cast_members, m.duration_minutes,
                       m.imdb_rating, m.summary, m.poster_url,
                       CASE WHEN wl.movie_id IS NULL THEN FALSE ELSE TRUE END AS watch_later
                FROM movies m
                LEFT JOIN watch_later wl ON wl.movie_id = m.id
                WHERE m.id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return toMovie(resultSet);
                }
                throw new SQLException("Movie not found.");
            }
        }
    }

    private void bindValues(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int index = 0; index < values.size(); index++) {
            statement.setObject(index + 1, values.get(index));
        }
    }

    private Movie toMovie(ResultSet resultSet) throws SQLException {
        return new Movie(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("genres"),
                resultSet.getString("cast_members"),
                resultSet.getInt("duration_minutes"),
                resultSet.getDouble("imdb_rating"),
                resultSet.getString("summary"),
                resultSet.getString("poster_url"),
                resultSet.getBoolean("watch_later")
        );
    }
}
