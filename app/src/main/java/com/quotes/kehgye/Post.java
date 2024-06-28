package com.quotes.kehgye;
public class Post {
    private String imageUrl;
    private String description;
    private String userId;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String imageUrl, String description, String userId) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
