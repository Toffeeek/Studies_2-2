package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MovieScoutApplication extends Application {
    private final MovieDAO movieDAO = new MovieDAO();
    private final WatchLaterDAO watchLaterDAO = new WatchLaterDAO();

    private BorderPane root;
    private FlowPane movieGrid;
    private TextField searchField;
    private ComboBox<String> genreBox;
    private Button allButton;
    private Button watchButton;
    private Button themeButton;
    private Label statusLabel;
    private boolean watchLaterOnly;
    private boolean darkMode = true;

    @Override
    public void start(Stage stage) {
        try {
            movieDAO.countMovies();
        } catch (SQLException exception) {
            showFatalScreen(stage, exception);
            return;
        }

        root = loadMainView();

        Scene scene = new Scene(root, 1040, 720);

        stage.setTitle("Movie Scout");
        stage.setMinWidth(860);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();

        loadGenres();
        refreshMovies();
    }

    private BorderPane loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(MovieScoutApplication.class.getResource("/resources/movie-browser-view.fxml"));
            BorderPane viewRoot = loader.load();
            Map<String, Object> controls = loader.getNamespace();

            themeButton = getControl(controls, "themeButton", Button.class);
            allButton = getControl(controls, "allButton", Button.class);
            watchButton = getControl(controls, "watchButton", Button.class);
            searchField = getControl(controls, "searchField", TextField.class);
            genreBox = getComboBox(controls, "genreBox");
            movieGrid = getControl(controls, "movieGrid", FlowPane.class);
            statusLabel = getControl(controls, "statusLabel", Label.class);

            wireMainActions();
            return viewRoot;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load movie-browser-view.fxml", exception);
        }
    }

    private void wireMainActions() {
        themeButton.setOnAction(event -> toggleTheme());

        allButton.setOnAction(event -> {
            watchLaterOnly = false;
            updateModeButtons();
            refreshMovies();
        });

        watchButton.setOnAction(event -> {
            watchLaterOnly = true;
            updateModeButtons();
            refreshMovies();
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> refreshMovies());
        genreBox.setOnAction(event -> refreshMovies());
    }

    private void loadGenres() {
        try {
            genreBox.getItems().setAll("All Genres");
            genreBox.getItems().addAll(movieDAO.findGenres());
            genreBox.getSelectionModel().selectFirst();
        } catch (SQLException exception) {
            showError("Could not load genres", exception);
        }
    }

    private void refreshMovies() {
        if (movieGrid == null || searchField == null || genreBox == null) {
            return;
        }

        try {
            List<Movie> movies = movieDAO.findMovies(
                    searchField.getText(),
                    genreBox.getValue(),
                    watchLaterOnly
            );
            movieGrid.getChildren().setAll(movies.stream().map(this::createMovieCard).toList());
            statusLabel.setText(movies.size() + " movie" + (movies.size() == 1 ? "" : "s") + " shown");
        } catch (SQLException exception) {
            showError("Could not load movies", exception);
        }
    }

    private VBox createMovieCard(Movie movie) {
        StackPane posterPane = createPoster(movie, 214, 300);

        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("movie-title");
        title.setWrapText(true);

        Label year = new Label("2025");
        year.getStyleClass().add("movie-year");

        FlowPane chips = new FlowPane();
        chips.getStyleClass().add("genre-chip-row");
        chips.setHgap(6);
        chips.setVgap(6);
        for (String genre : movie.getGenres().split(",")) {
            Label chip = new Label(genre.trim());
            chip.getStyleClass().add("genre-chip");
            chips.getChildren().add(chip);
        }

        Label star = new Label("★");
        star.getStyleClass().add("rating-star");

        Label rating = new Label(String.format("%.1f", movie.getImdbRating()));
        rating.getStyleClass().add("rating-label");

        Label votes = new Label(voteCount(movie) + " votes");
        votes.getStyleClass().add("vote-label");

        HBox ratingRow = new HBox(5, star, rating, votes);
        ratingRow.getStyleClass().add("rating-row");
        ratingRow.setAlignment(Pos.CENTER_LEFT);

        VBox body = new VBox(6, title, year, chips);
        body.getStyleClass().add("movie-card-body");
        VBox.setVgrow(body, javafx.scene.layout.Priority.ALWAYS);

        VBox card = new VBox(10, posterPane, body, ratingRow);
        card.getStyleClass().add("movie-card");
        card.setOnMouseClicked(event -> showDetails(movie.getId()));
        return card;
    }

    private int voteCount(Movie movie) {
        return 250 + (movie.getId() * 37) % 1200;
    }

    private StackPane createPoster(Movie movie, double width, double height) {
        ImageView poster = new ImageView();
        poster.setFitWidth(width);
        poster.setFitHeight(height);
        poster.setPreserveRatio(false);
        poster.setImage(new Image(movie.getPosterUrl(), width, height, false, true, true));

        Label fallback = new Label(movie.getTitle());
        fallback.getStyleClass().add("poster-fallback");
        fallback.setWrapText(true);
        fallback.setAlignment(Pos.CENTER);
        fallback.setMaxWidth(width - 24);

        StackPane posterPane = new StackPane(fallback, poster);
        posterPane.getStyleClass().add("poster-pane");
        posterPane.setPrefSize(width, height);
        posterPane.setMinSize(width, height);
        posterPane.setMaxSize(width, height);
        return posterPane;
    }

    private void showDetails(int movieId) {
        try {
            Movie movie = movieDAO.findById(movieId);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(movie.getTitle());

            FXMLLoader loader = new FXMLLoader(MovieScoutApplication.class.getResource("/resources/movie-detail-view.fxml"));
            HBox layout = loader.load();
            layout.getStyleClass().add(darkMode ? "dark" : "light");
            Map<String, Object> controls = loader.getNamespace();

            StackPane posterSlot = getControl(controls, "posterSlot", StackPane.class);
            Label title = getControl(controls, "detailTitle", Label.class);
            Label meta = getControl(controls, "detailMeta", Label.class);
            Label genres = getControl(controls, "detailGenres", Label.class);
            Label cast = getControl(controls, "detailCast", Label.class);
            Label summary = getControl(controls, "detailSummary", Label.class);
            Button watchLaterButton = getControl(controls, "detailWatchLaterButton", Button.class);
            Button closeButton = getControl(controls, "detailCloseButton", Button.class);

            posterSlot.getChildren().setAll(createPoster(movie, 220, 320));
            title.setText(movie.getTitle());
            title.setWrapText(true);

            meta.setText(movie.getDurationMinutes() + " minutes  |  IMDb " + String.format("%.1f", movie.getImdbRating()));
            genres.setText(movie.getGenres());
            genres.setWrapText(true);

            cast.setText("Cast: " + movie.getCast());
            cast.setWrapText(true);

            summary.setText(movie.getSummary());
            summary.setWrapText(true);

            watchLaterButton.setText(movie.isWatchLater() ? "Remove from Watch Later" : "Add to Watch Later");
            watchLaterButton.getStyleClass().add(movie.isWatchLater() ? "danger-button" : "primary-button");
            watchLaterButton.setOnAction(event -> {
                toggleWatchLater(movie);
                dialog.close();
            });

            closeButton.setOnAction(event -> dialog.close());

            Scene scene = new Scene(layout, 740, 420);
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException | SQLException exception) {
            showError("Could not load movie details", exception);
        }
    }

    private void toggleWatchLater(Movie movie) {
        try {
            if (movie.isWatchLater()) {
                watchLaterDAO.removeMovie(movie.getId());
                statusLabel.setText("Removed from Watch Later: " + movie.getTitle());
            } else {
                watchLaterDAO.addMovie(movie.getId());
                statusLabel.setText("Added to Watch Later: " + movie.getTitle());
            }
            refreshMovies();
        } catch (SQLException exception) {
            showError("Could not update Watch Later", exception);
        }
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        root.getStyleClass().setAll("app-root", darkMode ? "dark" : "light");
        themeButton.setText(darkMode ? "Dark" : "Light");
    }

    private void updateModeButtons() {
        allButton.getStyleClass().setAll("nav-button");
        watchButton.getStyleClass().setAll("nav-button");
        if (watchLaterOnly) {
            watchButton.getStyleClass().add("active");
        } else {
            allButton.getStyleClass().add("active");
        }
    }

    private void showFatalScreen(Stage stage, SQLException exception) {
        Label message = new Label(
                "Database is not ready. Run the DatabaseServer project first.\n"
                        + "Main class: server.DatabaseServerApplication\n\n"
                        + "Details: " + exception.getMessage()
        );
        message.getStyleClass().add("fatal-label");
        message.setWrapText(true);

        BorderPane fatalRoot = new BorderPane(message);
        fatalRoot.getStyleClass().addAll("app-root", "light");
        fatalRoot.setPadding(new Insets(32));

        Scene scene = new Scene(fatalRoot, 720, 280);
        stage.setTitle("Movie Scout");
        stage.setScene(scene);
        stage.show();
    }

    private void showError(String title, Exception exception) {
        statusLabel.setText(title + ": " + exception.getMessage());
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(title);
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        });
    }

    private <T> T getControl(Map<String, Object> controls, String id, Class<T> type) {
        return type.cast(controls.get(id));
    }

    @SuppressWarnings("unchecked")
    private ComboBox<String> getComboBox(Map<String, Object> controls, String id) {
        return (ComboBox<String>) controls.get(id);
    }
}
