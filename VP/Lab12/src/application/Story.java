package application;

public class Story {
    private final int id;
    private final String username;
    private final String displayName;
    private final String avatarUrl;
    private final String imageUrl;
    private final boolean ownStory;
    private final boolean unseen;

    public Story(int id, String username, String displayName, String avatarUrl, String imageUrl, boolean ownStory, boolean unseen) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.imageUrl = imageUrl;
        this.ownStory = ownStory;
        this.unseen = unseen;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isOwnStory() {
        return ownStory;
    }

    public boolean isUnseen() {
        return unseen;
    }

    public Story markSeen() {
        return new Story(id, username, displayName, avatarUrl, imageUrl, ownStory, false);
    }
}
