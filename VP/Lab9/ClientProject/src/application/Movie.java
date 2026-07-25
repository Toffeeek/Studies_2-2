package application;

public class Movie {
    private final int id;
    private final String title;
    private final String genres;
    private final String cast;
    private final int durationMinutes;
    private final double imdbRating;
    private final String summary;
    private final String posterUrl;
    private final boolean watchLater;

    public Movie(
            int id,
            String title,
            String genres,
            String cast,
            int durationMinutes,
            double imdbRating,
            String summary,
            String posterUrl,
            boolean watchLater
    ) {
        this.id = id;
        this.title = title;
        this.genres = genres;
        this.cast = cast;
        this.durationMinutes = durationMinutes;
        this.imdbRating = imdbRating;
        this.summary = summary;
        this.posterUrl = posterUrl;
        this.watchLater = watchLater;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenres() {
        return genres;
    }

    public String getCast() {
        return cast;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public String getSummary() {
        return summary;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public boolean isWatchLater() {
        return watchLater;
    }
}
