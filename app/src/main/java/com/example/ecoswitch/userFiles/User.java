package com.example.ecoswitch.userFiles;

public class User {
    private String name = "N/A";
    private String email = "N/A";
    private String location = "N/A";
    private String userId = "N/A"; // Default value set to 0, assuming user IDs will be Long type

    // Default constructor required for Firebase Firestore
    public User() {
    }

    // Constructor with name, email, location, and userId
    public User(String name, String email, String location, String userId) {
        this.name = name;
        this.email = email;
        this.location = location;
        this.userId = userId;
    }

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }



    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserId() {
        return userId;
    }


}
