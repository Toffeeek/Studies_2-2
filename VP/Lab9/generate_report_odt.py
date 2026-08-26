from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED, ZIP_STORED
from xml.sax.saxutils import escape

from PIL import Image


ROOT = Path(__file__).resolve().parent
OUT = ROOT / "Movie_Scout_Lab_Report.odt"
ASSETS = ROOT / "report_assets"

NS = {
    "office": "urn:oasis:names:tc:opendocument:xmlns:office:1.0",
    "style": "urn:oasis:names:tc:opendocument:xmlns:style:1.0",
    "text": "urn:oasis:names:tc:opendocument:xmlns:text:1.0",
    "table": "urn:oasis:names:tc:opendocument:xmlns:table:1.0",
    "draw": "urn:oasis:names:tc:opendocument:xmlns:drawing:1.0",
    "fo": "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0",
    "xlink": "http://www.w3.org/1999/xlink",
    "dc": "http://purl.org/dc/elements/1.1/",
    "meta": "urn:oasis:names:tc:opendocument:xmlns:meta:1.0",
    "svg": "urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0",
    "manifest": "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0",
}


def attrs(**items):
    return " ".join(f'{key}="{escape(str(value))}"' for key, value in items.items())


def p(text, style="Body"):
    return f'<text:p text:style-name="{style}">{escape(text)}</text:p>'


def h(text, level):
    return f'<text:h text:outline-level="{level}">{escape(text)}</text:h>'


def code_block(text):
    lines = escape(text).split("\n")
    joined = "<text:line-break/>".join(lines)
    return f'<text:p text:style-name="Code">{joined}</text:p>'


def bullet(items):
    out = ['<text:list text:style-name="List">']
    for item in items:
        out.append(f"<text:list-item>{p(item)}</text:list-item>")
    out.append("</text:list>")
    return "\n".join(out)


def table(rows):
    out = ['<table:table table:name="Table">']
    for row in rows:
        out.append("<table:table-row>")
        for cell in row:
            out.append("<table:table-cell office:value-type=\"string\">")
            out.append(p(cell))
            out.append("</table:table-cell>")
        out.append("</table:table-row>")
    out.append("</table:table>")
    return "\n".join(out)


def image(name, caption, max_width_in=6.6):
    path = ASSETS / name
    with Image.open(path) as img:
        width_px, height_px = img.size
    height_in = max_width_in * height_px / width_px
    if height_in > 8.2:
        height_in = 8.2
        max_width_in = height_in * width_px / height_px
    href = f"Pictures/{name}"
    return f"""
<text:p text:style-name="Figure">
  <draw:frame draw:name="{escape(name)}" text:anchor-type="paragraph" svg:width="{max_width_in:.2f}in" svg:height="{height_in:.2f}in">
    <draw:image xlink:href="{href}" xlink:type="simple" xlink:show="embed" xlink:actuate="onLoad"/>
  </draw:frame>
</text:p>
{p(caption, "Caption")}
"""


def content_xml():
    body = []
    body.append(h("Movie Scout Lab Report", 1))
    body.append(p("Project: Movie Browser with JavaFX, DAO Classes, and MariaDB", "Center"))
    body.append(p("Database: vpl_lab", "Center"))
    body.append(p("Module: Lab9 / DatabaseServerProject", "Center"))

    body.append(h("1. Problem Statement", 2))
    body.append(p("The lab task was to prepare a movie browsing application and explain the database schema, DAO classes, Java classes, database queries, dynamically added UI components, FXML files, application screenshots, and database schema evidence."))
    body.append(image("problem_statement.png", "Figure 1: Given lab report requirements."))

    body.append(h("2. Project Overview", 2))
    body.append(p("Movie Scout is a JavaFX desktop application that displays a collection of movies from a MariaDB database. The application supports browsing all movies, searching by movie title, filtering by genre, viewing movie details, and adding or removing movies from a Watch Later list."))
    body.append(table([
        ["Part", "Description"],
        ["DatabaseServerProject", "Initializes the vpl_lab database, creates tables, and seeds movie records."],
        ["src/application", "JavaFX client application. It loads movies through DAO classes and builds the movie browsing UI."],
    ]))

    body.append(h("3. Database Schema", 2))
    body.append(p("The database contains two tables: movies and watch_later. The movies table stores all movie information. The watch_later table stores selected movie IDs and references movies.id."))
    body.append(p("Relationship: movies.id 1 --- 0..1 watch_later.movie_id. The foreign key uses ON DELETE CASCADE, so deleting a movie also deletes its Watch Later row."))
    body.append(table([
        ["movies column", "Type", "Constraint", "Purpose"],
        ["id", "INT", "Primary key, auto increment", "Unique movie identifier."],
        ["title", "VARCHAR(160)", "Not null, unique", "Movie title."],
        ["genres", "VARCHAR(180)", "Not null", "Comma-separated movie genres."],
        ["cast_members", "VARCHAR(260)", "Not null", "Main cast list."],
        ["duration_minutes", "INT", "Not null", "Runtime in minutes."],
        ["imdb_rating", "DECIMAL(3,1)", "Not null", "IMDb rating."],
        ["summary", "TEXT", "Not null", "Movie description."],
        ["poster_url", "VARCHAR(500)", "Not null", "Poster image URL."],
    ]))
    body.append(table([
        ["watch_later column", "Type", "Constraint", "Purpose"],
        ["movie_id", "INT", "Primary key, foreign key to movies(id)", "Selected movie."],
        ["created_at", "TIMESTAMP", "Default current timestamp", "Time when the movie was added."],
    ]))
    body.append(code_block("""CREATE TABLE IF NOT EXISTS movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(160) NOT NULL UNIQUE,
    genres VARCHAR(180) NOT NULL,
    cast_members VARCHAR(260) NOT NULL,
    duration_minutes INT NOT NULL,
    imdb_rating DECIMAL(3,1) NOT NULL,
    summary TEXT NOT NULL,
    poster_url VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS watch_later (
    movie_id INT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_watch_later_movie
        FOREIGN KEY (movie_id) REFERENCES movies(id)
        ON DELETE CASCADE
);"""))

    body.append(h("4. Database Evidence", 2))
    body.append(image("db_tables.png", "Figure 2: MariaDB output showing the movies and watch_later tables."))
    body.append(image("db_movies.png", "Figure 3: Movie records stored in the movies table."))
    body.append(image("db_watch_later.png", "Figure 4: Records stored in the watch_later table."))

    body.append(h("5. DAO Classes and Queries", 2))
    body.append(h("5.1 DBConnection", 3))
    body.append(p("DBConnection centralizes JDBC connection creation. Both the database server module and the JavaFX client use JDBC to connect to the local MariaDB server."))
    body.append(code_block("""public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
}"""))

    body.append(h("5.2 MovieSeedDAO", 3))
    body.append(p("MovieSeedDAO prepares the database for the client application. It creates the database, creates tables, inserts seed data, and counts movies after setup."))
    body.append(code_block("""public void initializeDatabase() throws SQLException {
    createDatabase();
    createTables();
    seedMovies();
}"""))
    body.append(p("The seed query uses INSERT IGNORE. This prevents duplicate records because title is unique in the movies table."))
    body.append(code_block("""INSERT IGNORE INTO movies
(title, genres, cast_members, duration_minutes, imdb_rating, summary, poster_url)
VALUES (?, ?, ?, ?, ?, ?, ?)"""))

    body.append(h("5.3 MovieDAO", 3))
    body.append(p("MovieDAO reads movie data from the database. It contains methods for counting movies, searching movies, loading genres, and finding a movie by ID."))
    body.append(p("Counting movies:"))
    body.append(code_block("SELECT COUNT(*) AS movie_count FROM movies"))
    body.append(p("Searching and filtering movies:"))
    body.append(code_block("""SELECT m.id, m.title, m.genres, m.cast_members, m.duration_minutes,
       m.imdb_rating, m.summary, m.poster_url,
       CASE WHEN wl.movie_id IS NULL THEN FALSE ELSE TRUE END AS watch_later
FROM movies m
LEFT JOIN watch_later wl ON wl.movie_id = m.id
WHERE LOWER(m.title) LIKE LOWER(?)
ORDER BY m.imdb_rating DESC, m.title"""))
    body.append(p("The LEFT JOIN allows the application to show whether each movie is already in Watch Later. The LIKE condition supports title search. Genre and Watch Later filters append extra WHERE conditions."))
    body.append(p("Loading genres:"))
    body.append(code_block("SELECT genres FROM movies ORDER BY genres"))
    body.append(p("The DAO splits comma-separated genres in Java, removes duplicates, and fills the genre combo box."))
    body.append(p("Finding a movie by ID:"))
    body.append(code_block("WHERE m.id = ?"))
    body.append(p("This prepared statement is used when a movie card is clicked and the detail dialog opens."))

    body.append(h("5.4 WatchLaterDAO", 3))
    body.append(p("WatchLaterDAO manages insert and delete operations for the Watch Later list."))
    body.append(code_block("INSERT IGNORE INTO watch_later (movie_id) VALUES (?)"))
    body.append(p("This query adds a movie to Watch Later. INSERT IGNORE avoids duplicate insert errors because movie_id is the primary key."))
    body.append(code_block("DELETE FROM watch_later WHERE movie_id = ?"))
    body.append(p("This query removes a movie from Watch Later."))

    body.append(h("6. Java Classes", 2))
    body.append(table([
        ["Class", "Responsibility"],
        ["Main", "Launches the JavaFX application."],
        ["MovieScoutApplication", "Loads FXML, wires events, creates movie cards dynamically, opens detail dialogs, and handles errors."],
        ["Movie", "Model class for movie data such as title, genres, cast, duration, rating, summary, poster URL, and watch-later status."],
        ["MovieDAO", "Reads movie data, performs search/filter queries, and maps result sets to Movie objects."],
        ["WatchLaterDAO", "Adds and removes movies in the watch_later table."],
        ["DatabaseServerApplication", "Runs the database setup process before the client is used."],
        ["MovieSeedDAO", "Creates schema and inserts initial movie data."],
    ]))

    body.append(h("7. Dynamic UI Components", 2))
    body.append(p("The FXML file defines the main layout containers, search field, genre combo box, status label, and empty movie grid. The movie cards are generated dynamically in Java after records are loaded from the database."))
    body.append(code_block("""movieGrid.getChildren().setAll(
        movies.stream().map(this::createMovieCard).toList()
);"""))
    body.append(p("Each movie card is created by createMovieCard(Movie movie). The method creates a poster, title label, year label, genre chips, rating row, and click handler."))
    body.append(code_block("""VBox card = new VBox(10, posterPane, body, ratingRow);
card.getStyleClass().add("movie-card");
card.setOnMouseClicked(event -> showDetails(movie.getId()));"""))
    body.append(p("Genre chips are dynamic because the code splits each movie genre string and creates a new Label for each genre."))
    body.append(code_block("""for (String genre : movie.getGenres().split(",")) {
    Label chip = new Label(genre.trim());
    chip.getStyleClass().add("genre-chip");
    chips.getChildren().add(chip);
}"""))

    body.append(h("8. FXML Files", 2))
    body.append(h("8.1 movie-browser-view.fxml", 3))
    body.append(p("This FXML file defines the main browser screen: top bar, theme button, navigation buttons, search field, genre combo box, movie grid, and status bar."))
    body.append(code_block("""<TextField fx:id="searchField" promptText="Search movie..." />
<ComboBox fx:id="genreBox" promptText="All Genres" />
<FlowPane fx:id="movieGrid" hgap="18.0" vgap="22.0" />
<Label fx:id="statusLabel" text="Ready" />"""))
    body.append(h("8.2 movie-detail-view.fxml", 3))
    body.append(p("This FXML file defines the movie detail dialog layout. Java fills the controls after a movie card is clicked."))
    body.append(code_block("""<StackPane fx:id="posterSlot" prefHeight="320.0" prefWidth="220.0" />
<Label fx:id="detailTitle" wrapText="true" />
<Label fx:id="detailMeta" />
<Label fx:id="detailGenres" wrapText="true" />
<Button fx:id="detailWatchLaterButton" text="Add to Watch Later" />"""))

    body.append(h("9. Application Screenshots", 2))
    body.append(image("home_screen.png", "Figure 5: Home screen showing all popular movies loaded from the database."))
    body.append(image("search_feature.png", "Figure 6: Search feature filtering movies by title text."))
    body.append(image("movie_details.png", "Figure 7: Movie detail dialog with poster, metadata, cast, summary, and Watch Later button."))
    body.append(image("watch_later.png", "Figure 8: Watch Later view showing movies saved in the watch_later table."))

    body.append(h("10. Conclusion", 2))
    body.append(p("The project satisfies the movie browsing task by combining a JavaFX user interface with a MariaDB database backend. The database server module initializes the schema and seed data, while the client uses DAO classes to run prepared SQL queries. The application dynamically generates movie cards, supports live search and filtering, displays detailed movie information, and persists Watch Later selections in the database."))

    ns_attrs = " ".join(f'xmlns:{k}="{v}"' for k, v in NS.items() if k != "manifest")
    return f"""<?xml version="1.0" encoding="UTF-8"?>
<office:document-content {ns_attrs} office:version="1.2">
  <office:automatic-styles>
    <style:style style:name="Body" style:family="paragraph"><style:text-properties fo:font-size="12pt"/></style:style>
    <style:style style:name="Center" style:family="paragraph"><style:paragraph-properties fo:text-align="center"/></style:style>
    <style:style style:name="Code" style:family="paragraph"><style:paragraph-properties fo:background-color="#f3f4f6" fo:padding="0.08in"/><style:text-properties style:font-name="Liberation Mono" fo:font-size="9pt"/></style:style>
    <style:style style:name="Caption" style:family="paragraph"><style:paragraph-properties fo:text-align="center"/><style:text-properties fo:font-size="10pt" fo:font-style="italic"/></style:style>
    <style:style style:name="Figure" style:family="paragraph"><style:paragraph-properties fo:text-align="center"/></style:style>
    <text:list-style style:name="List"><text:list-level-style-bullet text:level="1" text:bullet-char="*"/></text:list-style>
  </office:automatic-styles>
  <office:body>
    <office:text>
      {''.join(body)}
    </office:text>
  </office:body>
</office:document-content>
"""


def styles_xml():
    ns_attrs = " ".join(f'xmlns:{k}="{v}"' for k, v in NS.items() if k != "manifest")
    return f"""<?xml version="1.0" encoding="UTF-8"?>
<office:document-styles {ns_attrs} office:version="1.2">
  <office:font-face-decls>
    <style:font-face style:name="Liberation Serif" svg:font-family="Liberation Serif"/>
    <style:font-face style:name="Liberation Sans" svg:font-family="Liberation Sans"/>
    <style:font-face style:name="Liberation Mono" svg:font-family="Liberation Mono"/>
  </office:font-face-decls>
  <office:styles/>
  <office:automatic-styles>
    <style:page-layout style:name="pm1">
      <style:page-layout-properties fo:page-width="8.27in" fo:page-height="11.69in" fo:margin-top="0.7in" fo:margin-bottom="0.7in" fo:margin-left="0.7in" fo:margin-right="0.7in"/>
    </style:page-layout>
  </office:automatic-styles>
  <office:master-styles>
    <style:master-page style:name="Standard" style:page-layout-name="pm1"/>
  </office:master-styles>
</office:document-styles>
"""


def manifest_xml():
    entries = [
        '<manifest:file-entry manifest:full-path="/" manifest:media-type="application/vnd.oasis.opendocument.text"/>',
        '<manifest:file-entry manifest:full-path="content.xml" manifest:media-type="text/xml"/>',
        '<manifest:file-entry manifest:full-path="styles.xml" manifest:media-type="text/xml"/>',
        '<manifest:file-entry manifest:full-path="meta.xml" manifest:media-type="text/xml"/>',
        '<manifest:file-entry manifest:full-path="settings.xml" manifest:media-type="text/xml"/>',
    ]
    for image_path in sorted(ASSETS.glob("*.png")):
        entries.append(f'<manifest:file-entry manifest:full-path="Pictures/{image_path.name}" manifest:media-type="image/png"/>')
    return f"""<?xml version="1.0" encoding="UTF-8"?>
<manifest:manifest xmlns:manifest="{NS["manifest"]}" manifest:version="1.2">
{''.join(entries)}
</manifest:manifest>
"""


def meta_xml():
    ns_attrs = " ".join(f'xmlns:{k}="{v}"' for k, v in NS.items() if k in {"office", "dc", "meta"})
    return f"""<?xml version="1.0" encoding="UTF-8"?>
<office:document-meta {ns_attrs} office:version="1.2">
  <office:meta>
    <dc:title>Movie Scout Lab Report</dc:title>
    <meta:generator>Codex generated OpenDocument report</meta:generator>
  </office:meta>
</office:document-meta>
"""


def settings_xml():
    return f"""<?xml version="1.0" encoding="UTF-8"?>
<office:document-settings xmlns:office="{NS["office"]}" office:version="1.2">
  <office:settings/>
</office:document-settings>
"""


def main():
    with ZipFile(OUT, "w") as odt:
        odt.writestr("mimetype", "application/vnd.oasis.opendocument.text", compress_type=ZIP_STORED)
        odt.writestr("content.xml", content_xml(), compress_type=ZIP_DEFLATED)
        odt.writestr("styles.xml", styles_xml(), compress_type=ZIP_DEFLATED)
        odt.writestr("meta.xml", meta_xml(), compress_type=ZIP_DEFLATED)
        odt.writestr("settings.xml", settings_xml(), compress_type=ZIP_DEFLATED)
        odt.writestr("META-INF/manifest.xml", manifest_xml(), compress_type=ZIP_DEFLATED)
        for image_path in sorted(ASSETS.glob("*.png")):
            odt.write(image_path, f"Pictures/{image_path.name}", compress_type=ZIP_DEFLATED)


if __name__ == "__main__":
    main()
