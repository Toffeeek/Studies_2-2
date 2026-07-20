package server;

import server.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MovieSeedDAO {
    public void initializeDatabase() throws SQLException {
        createTables();
        seedMovies();
    }

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

    private void createTables() throws SQLException {
        String moviesSql = """
                CREATE TABLE IF NOT EXISTS movies (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(160) NOT NULL UNIQUE,
                    genres VARCHAR(180) NOT NULL,
                    cast_members VARCHAR(260) NOT NULL,
                    duration_minutes INT NOT NULL,
                    imdb_rating DECIMAL(3,1) NOT NULL,
                    summary TEXT NOT NULL,
                    poster_url VARCHAR(500) NOT NULL
                )
                """;
        String watchLaterSql = """
                CREATE TABLE IF NOT EXISTS watch_later (
                    movie_id INT PRIMARY KEY,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_watch_later_movie
                        FOREIGN KEY (movie_id) REFERENCES movies(id)
                        ON DELETE CASCADE
                )
                """;

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(moviesSql);
            statement.execute(watchLaterSql);
        }
    }

    private void seedMovies() throws SQLException {
        String sql = """
                INSERT IGNORE INTO movies
                (title, genres, cast_members, duration_minutes, imdb_rating, summary, poster_url)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Object[] movie : seedData()) {
                statement.setString(1, (String) movie[0]);
                statement.setString(2, (String) movie[1]);
                statement.setString(3, (String) movie[2]);
                statement.setInt(4, (Integer) movie[3]);
                statement.setDouble(5, (Double) movie[4]);
                statement.setString(6, (String) movie[5]);
                statement.setString(7, (String) movie[6]);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private List<Object[]> seedData() {
        return List.of(
                movie("How to Train Your Dragon", "Animation, Adventure, Family", "Mason Thames, Nico Parker, Gerard Butler", 125, 7.9, "A young Viking befriends an injured dragon and discovers that courage can change an entire village.", "https://image.tmdb.org/t/p/w342/q5pXRYTycaeW6dEgsCrd4mYPmxM.jpg"),
                movie("Demon Slayer: Kimetsu no Yaiba Infinity Castle", "Animation, Action, Fantasy", "Natsuki Hanae, Akari Kito, Hiro Shimono", 155, 8.7, "Tanjiro and the Demon Slayer Corps enter Muzan's stronghold for a decisive supernatural battle.", "https://image.tmdb.org/t/p/w342/2n7lYEeIbucsEQCswRcVB6ZYmMP.jpg"),
                movie("Lilo & Stitch", "Adventure, Comedy, Family", "Maia Kealoha, Sydney Agudong, Zach Galifianakis", 108, 7.0, "A lonely Hawaiian girl adopts a chaotic alien and teaches him the meaning of family.", "https://image.tmdb.org/t/p/w342/3bN675X0K2E5QiAZVChzB5wq90B.jpg"),
                movie("M3GAN 2.0", "Horror, Sci-Fi, Thriller", "Allison Williams, Violet McGraw, Amie Donald", 120, 6.5, "The artificial-intelligence doll returns as a new technological threat forces old enemies into uneasy cooperation.", "https://image.tmdb.org/t/p/w342/4a63rQqIDTrYNdcnTXdPsQyxVLo.jpg"),
                movie("Inception", "Action, Sci-Fi, Thriller", "Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page", 148, 8.8, "A skilled thief enters dreams to plant an idea that could alter the future of a business empire.", "https://image.tmdb.org/t/p/w342/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg"),
                movie("Interstellar", "Adventure, Drama, Sci-Fi", "Matthew McConaughey, Anne Hathaway, Jessica Chastain", 169, 8.7, "Explorers travel through a wormhole searching for a new home as Earth nears collapse.", "https://image.tmdb.org/t/p/w342/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"),
                movie("The Dark Knight", "Action, Crime, Drama", "Christian Bale, Heath Ledger, Aaron Eckhart", 152, 9.0, "Batman faces the Joker, a criminal mastermind determined to prove Gotham can be broken.", "https://image.tmdb.org/t/p/w342/qJ2tW6WMUDux911r6m7haRef0WH.jpg"),
                movie("Spider-Man: Into the Spider-Verse", "Animation, Action, Adventure", "Shameik Moore, Hailee Steinfeld, Jake Johnson", 117, 8.4, "Miles Morales becomes Spider-Man and joins heroes from parallel worlds to save Brooklyn.", "https://image.tmdb.org/t/p/w342/iiZZdoQBEYBv6id8su7ImL0oCbD.jpg"),
                movie("Dune: Part Two", "Adventure, Drama, Sci-Fi", "Timothee Chalamet, Zendaya, Rebecca Ferguson", 166, 8.5, "Paul Atreides unites with the Fremen while choosing between love, revenge, and destiny.", "https://image.tmdb.org/t/p/w342/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg"),
                movie("Inside Out 2", "Animation, Comedy, Family", "Amy Poehler, Maya Hawke, Kensington Tallman", 96, 7.6, "Riley enters her teenage years as new emotions arrive and reshape headquarters.", "https://image.tmdb.org/t/p/w342/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg"),
                movie("The Batman", "Action, Crime, Mystery", "Robert Pattinson, Zoe Kravitz, Paul Dano", 176, 7.8, "Batman investigates a trail of corruption after the Riddler targets Gotham's elite.", "https://image.tmdb.org/t/p/w342/74xTEgt7R36Fpooo50r9T25onhq.jpg"),
                movie("Avatar: The Way of Water", "Action, Adventure, Fantasy", "Sam Worthington, Zoe Saldana, Sigourney Weaver", 192, 7.5, "Jake Sully's family seeks refuge with ocean clans as a familiar threat returns to Pandora.", "https://image.tmdb.org/t/p/w342/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg"),
                movie("Toy Story", "Animation, Adventure, Comedy", "Tom Hanks, Tim Allen, Don Rickles", 81, 8.3, "A pull-string cowboy feels threatened when a space ranger becomes his owner's favorite toy.", "https://image.tmdb.org/t/p/w342/uXDfjJbdP4ijW5hWSBrPrlKpxab.jpg"),
                movie("Spirited Away", "Animation, Adventure, Fantasy", "Rumi Hiiragi, Miyu Irino, Mari Natsuki", 125, 8.6, "A girl trapped in a spirit world must work in a bathhouse to rescue her parents.", "https://image.tmdb.org/t/p/w342/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg"),
                movie("Parasite", "Drama, Thriller", "Song Kang-ho, Lee Sun-kyun, Cho Yeo-jeong", 132, 8.5, "A poor family infiltrates a wealthy household, exposing class conflict with dangerous consequences.", "https://image.tmdb.org/t/p/w342/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg"),
                movie("Oppenheimer", "Biography, Drama, History", "Cillian Murphy, Emily Blunt, Robert Downey Jr.", 180, 8.3, "J. Robert Oppenheimer leads the Manhattan Project and later confronts its political fallout.", "https://image.tmdb.org/t/p/w342/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg"),
                movie("Barbie", "Adventure, Comedy, Fantasy", "Margot Robbie, Ryan Gosling, America Ferrera", 114, 6.8, "Barbie leaves her perfect world for a vivid journey through identity and self-discovery.", "https://image.tmdb.org/t/p/w342/iuFNMS8U5cb6xfzi51Dbkovj7vM.jpg"),
                movie("John Wick: Chapter 4", "Action, Crime, Thriller", "Keanu Reeves, Donnie Yen, Bill Skarsgard", 169, 7.7, "John Wick fights his way through global assassins to earn freedom from the High Table.", "https://image.tmdb.org/t/p/w342/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg"),
                movie("Godzilla x Kong: The New Empire", "Action, Adventure, Sci-Fi", "Rebecca Hall, Brian Tyree Henry, Dan Stevens", 115, 6.1, "Godzilla and Kong face a colossal hidden threat rising from the Hollow Earth.", "https://image.tmdb.org/t/p/w342/z1p34vh7dEOnLDmyCrlUVLuoDzd.jpg"),
                movie("Mission: Impossible - Dead Reckoning", "Action, Adventure, Thriller", "Tom Cruise, Hayley Atwell, Ving Rhames", 163, 7.7, "Ethan Hunt races against a powerful AI weapon before it reshapes global power.", "https://image.tmdb.org/t/p/w342/NNxYkU70HPurnNCSiCjYAmacwm.jpg")
        );
    }

    private Object[] movie(String title, String genres, String cast, int duration, double rating, String summary, String posterUrl) {
        return new Object[]{title, genres, cast, duration, rating, summary, posterUrl};
    }
}
