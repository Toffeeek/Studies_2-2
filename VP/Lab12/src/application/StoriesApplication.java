package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoriesApplication extends Application {
    private static final int STORY_LOAD_DELAY_MS = 1600;
    private static final int AUTO_ADVANCE_SECONDS = 5;

    private final StoryDAO storyDAO = new StoryDAO();
    private final List<Story> stories = new ArrayList<>();

    private BorderPane root;
    private HBox storyRow;
    private StackPane viewerPane;
    private ImageView storyImageView;
    private ProgressIndicator progressIndicator;
    private Label statusLabel;
    private Label titleLabel;
    private Label subtitleLabel;
    private Button leftButton;
    private Button rightButton;
    private Button deleteButton;
    private Button themeButton;
    private Timeline autoAdvanceTimeline;
    private boolean darkMode = true;
    private int currentIndex;

    @Override
    public void start(Stage stage) {
        try {
            storyDAO.countStories();
        } catch (SQLException exception) {
            showFatalScreen(stage, exception);
            return;
        }

        root = buildMainView();
        Scene scene = new Scene(root, 980, 680);
        scene.getStylesheets().add(StoriesApplication.class.getResource("/resources/styles.css").toExternalForm());

        stage.setTitle("Stories");
        stage.setMinWidth(820);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();

        loadStories();
    }

    private BorderPane buildMainView() {
        BorderPane layout = new BorderPane();
        layout.getStyleClass().addAll("app-root", "dark");

        Label brand = new Label("Stories");
        brand.getStyleClass().add("brand-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        themeButton = new Button("Light");
        themeButton.getStyleClass().add("small-button");
        themeButton.setOnAction(event -> toggleTheme());

        HBox topBar = new HBox(12, brand, spacer, themeButton);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        storyRow = new HBox(14);
        storyRow.getStyleClass().add("story-row");
        storyRow.setAlignment(Pos.CENTER_LEFT);

        VBox top = new VBox(topBar, storyRow);
        layout.setTop(top);

        viewerPane = new StackPane();
        viewerPane.getStyleClass().add("viewer-pane");
        layout.setCenter(viewerPane);
        buildViewer();

        return layout;
    }

    private void buildViewer() {
        storyImageView = new ImageView();
        storyImageView.setFitWidth(430);
        storyImageView.setFitHeight(560);
        storyImageView.setPreserveRatio(false);
        storyImageView.getStyleClass().add("story-image");

        titleLabel = new Label("Select a story");
        titleLabel.getStyleClass().add("viewer-title");

        subtitleLabel = new Label("The story row stays usable while images load in the background.");
        subtitleLabel.getStyleClass().add("viewer-subtitle");

        statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-label");

        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(54, 54);
        progressIndicator.setVisible(false);

        leftButton = new Button("<");
        leftButton.getStyleClass().add("arrow-button");
        leftButton.setOnAction(event -> showPreviousStory());

        rightButton = new Button(">");
        rightButton.getStyleClass().add("arrow-button");
        rightButton.setOnAction(event -> showNextStory());

        deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> deleteCurrentStory());

        HBox header = new HBox(10, titleLabel, subtitleLabel);
        header.getStyleClass().add("viewer-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Region toolbarSpacer = new Region();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, statusLabel, toolbarSpacer, deleteButton);
        toolbar.getStyleClass().add("viewer-toolbar");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        StackPane imageSlot = new StackPane(storyImageView, progressIndicator);
        imageSlot.getStyleClass().add("image-slot");
        imageSlot.setMaxSize(430, 560);

        BorderPane viewer = new BorderPane();
        viewer.getStyleClass().add("viewer");
        viewer.setTop(header);
        viewer.setCenter(imageSlot);
        viewer.setBottom(toolbar);
        viewer.setLeft(leftButton);
        viewer.setRight(rightButton);
        BorderPane.setAlignment(leftButton, Pos.CENTER);
        BorderPane.setAlignment(rightButton, Pos.CENTER);
        BorderPane.setMargin(leftButton, new Insets(0, 18, 0, 0));
        BorderPane.setMargin(rightButton, new Insets(0, 0, 0, 18));

        viewerPane.getChildren().setAll(viewer);
        deleteButton.setVisible(false);
        updateNavigationButtons();
    }

    private void loadStories() {
        statusLabel.setText("Loading stories...");
        Task<List<Story>> task = new Task<>() {
            @Override
            protected List<Story> call() throws Exception {
                storyDAO.resetAllStoriesToUnseen();
                return storyDAO.findStories();
            }
        };

        task.setOnSucceeded(event -> {
            stories.clear();
            stories.addAll(task.getValue());
            renderStoryRow();
            if (stories.isEmpty()) {
                clearViewer("No stories available");
            } else {
                currentIndex = Math.min(currentIndex, stories.size() - 1);
                loadStory(currentIndex);
            }
        });

        task.setOnFailed(event -> showError("Could not load stories", task.getException()));
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void renderStoryRow() {
        storyRow.getChildren().clear();
        for (int index = 0; index < stories.size(); index++) {
            Story story = stories.get(index);
            int storyIndex = index;
            storyRow.getChildren().add(createStoryIcon(story, storyIndex));
        }
    }

    private VBox createStoryIcon(Story story, int storyIndex) {
        ImageView avatar = new ImageView(new Image(story.getAvatarUrl(), 62, 62, false, true, true));
        avatar.setFitWidth(62);
        avatar.setFitHeight(62);
        avatar.setPreserveRatio(false);
        avatar.setClip(new Circle(31, 31, 31));

        StackPane ring = new StackPane(avatar);
        ring.getStyleClass().add(story.isUnseen() ? "avatar-ring-unseen" : "avatar-ring-seen");

        Label name = new Label(story.getDisplayName());
        name.getStyleClass().add("story-name");
        name.setMaxWidth(82);
        name.setAlignment(Pos.CENTER);

        VBox item = new VBox(7, ring, name);
        item.getStyleClass().add("story-item");
        item.setAlignment(Pos.CENTER);
        item.setOnMouseClicked(event -> loadStory(storyIndex));
        return item;
    }

    private void loadStory(int index) {
        if (stories.isEmpty()) {
            clearViewer("No stories available");
            return;
        }

        stopAutoAdvance();
        currentIndex = normalizeIndex(index);
        Story story = stories.get(currentIndex);
        markStorySeen(currentIndex, story);

        titleLabel.setText(story.getDisplayName());
        subtitleLabel.setText("@" + story.getUsername());
        statusLabel.setText("Loading story...");
        progressIndicator.setVisible(true);
        storyImageView.setOpacity(0.32);
        deleteButton.setVisible(story.isOwnStory());
        updateNavigationButtons();

        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                Thread.sleep(STORY_LOAD_DELAY_MS);
                return new Image(story.getImageUrl(), 430, 560, false, true, false);
            }
        };

        task.setOnSucceeded(event -> {
            if (!stories.isEmpty() && stories.get(currentIndex).getId() == story.getId()) {
                storyImageView.setImage(task.getValue());
                storyImageView.setOpacity(1);
                progressIndicator.setVisible(false);
                statusLabel.setText((currentIndex + 1) + " of " + stories.size());
                startAutoAdvance();
            }
        });

        task.setOnFailed(event -> {
            progressIndicator.setVisible(false);
            storyImageView.setOpacity(1);
            statusLabel.setText("Could not load this story");
            startAutoAdvance();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void markStorySeen(int index, Story story) {
        if (!story.isUnseen()) {
            return;
        }

        stories.set(index, story.markSeen());
        renderStoryRow();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                storyDAO.markSeen(story.getId());
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showPreviousStory() {
        loadStory(currentIndex - 1);
    }

    private void showNextStory() {
        loadStory(currentIndex + 1);
    }

    private void deleteCurrentStory() {
        if (stories.isEmpty()) {
            return;
        }

        Story story = stories.get(currentIndex);
        if (!story.isOwnStory()) {
            return;
        }

        stopAutoAdvance();
        statusLabel.setText("Deleting story...");
        deleteButton.setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                storyDAO.deleteStory(story.getId());
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            deleteButton.setDisable(false);
            stories.remove(currentIndex);
            renderStoryRow();
            if (stories.isEmpty()) {
                clearViewer("No stories available");
            } else {
                loadStory(Math.min(currentIndex, stories.size() - 1));
            }
        });

        task.setOnFailed(event -> {
            deleteButton.setDisable(false);
            showError("Could not delete story", task.getException());
            startAutoAdvance();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private int normalizeIndex(int index) {
        int size = stories.size();
        return ((index % size) + size) % size;
    }

    private void startAutoAdvance() {
        stopAutoAdvance();
        if (stories.size() < 2) {
            return;
        }

        autoAdvanceTimeline = new Timeline(new KeyFrame(Duration.seconds(AUTO_ADVANCE_SECONDS), event -> showNextStory()));
        autoAdvanceTimeline.setCycleCount(1);
        autoAdvanceTimeline.play();
    }

    private void stopAutoAdvance() {
        if (autoAdvanceTimeline != null) {
            autoAdvanceTimeline.stop();
        }
    }

    private void updateNavigationButtons() {
        boolean disabled = stories.size() < 2;
        leftButton.setDisable(disabled);
        rightButton.setDisable(disabled);
    }

    private void clearViewer(String message) {
        stopAutoAdvance();
        titleLabel.setText(message);
        subtitleLabel.setText("");
        statusLabel.setText("Ready");
        storyImageView.setImage(null);
        progressIndicator.setVisible(false);
        deleteButton.setVisible(false);
        updateNavigationButtons();
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        root.getStyleClass().removeAll("dark", "light");
        root.getStyleClass().add(darkMode ? "dark" : "light");
        themeButton.setText(darkMode ? "Light" : "Dark");
    }

    private void showFatalScreen(Stage stage, Exception exception) {
        Label title = new Label("Stories database is not ready");
        title.getStyleClass().add("fatal-title");
        Label message = new Label(exception.getMessage());
        message.getStyleClass().add("fatal-message");
        message.setWrapText(true);
        VBox layout = new VBox(12, title, message);
        layout.getStyleClass().addAll("app-root", "dark", "fatal-screen");
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 760, 420);
        scene.getStylesheets().add(StoriesApplication.class.getResource("/resources/styles.css").toExternalForm());
        stage.setTitle("Stories");
        stage.setScene(scene);
        stage.show();
    }

    private void showError(String title, Throwable exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }
}
