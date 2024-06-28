package com.quotes.kehgye;

public class User {
    private String userId;
    private String username;
    private String email;
    private String password;
    private String profilePictureUrl;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String userId, String username, String email, String password, String profilePictureUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getter and setter methods

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
