package com.example.ecoswitch.userFragments;

public class Photo {
    private String imageUrl;
    private double latitude;
    private double longitude;
    private String userId; // Add userId field
    boolean approved;

    public Photo() {
        // Default constructor required for Firestore
    }

    public Photo(String imageUrl, double latitude, double longitude, String userId) {
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId; // Initialize userId
        this.approved = false;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
