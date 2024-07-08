package com.quotes.kehgye;

import com.google.firebase.Timestamp;
import java.util.Date;

public class Quote {

    private String id;
    private String quoteText;
    private String imageUrl;
    private String caption;
    private int likes;
    private String userId;
    private String username;
    private boolean liked;
    private Timestamp timestamp;


    public Quote() {
        // Default constructor required for calls to DataSnapshot.getValue(Quote.class)
    }

    public Quote(String quoteText, String imageUrl, String caption, int likes, String userId, String username, Timestamp timestamp) {
        this.quoteText = quoteText;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.likes = likes;
        this.userId = userId;
        this.username = username;
        this.timestamp = timestamp;

        this.liked = false; // Initialize as not liked
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
