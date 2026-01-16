package com.example.ecoswitch.userFiles;

import com.google.firebase.Timestamp; // Correct import

public class Request {
    private String requestId; // Unique ID for the request
    private String userId;
    private String name;
    private String location;
    private String photoUrl; // Photo URL for this specific request
    private String classification;
    private Timestamp timeStamp; // Correct type (Firebase Timestamp)

    private String status; // Flag to track if the request has been approved

    public Request() {}

    public Request(String requestId, String userId, String name, String location, String photoUrl, String classification, Timestamp timeStamp, String status) { // Correct type
        this.requestId = requestId;
        this.userId = userId;
        this.name = name;
        this.location = location;
        this.status=status;
        this.photoUrl = photoUrl;
        this.classification = classification;
        this.timeStamp = timeStamp;
    }

    public String getRequestId() { return requestId; }
    public String getStatus() { return status; }
    public Timestamp getTimeStamp() { return timeStamp; } // Correct type


    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getPhotoUrl() { return photoUrl; }

    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setStatus(String status) { this.status = status; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setTimeStamp(Timestamp timeStamp) { this.timeStamp = timeStamp; } // Correct type

    public void setClassification(String classification) { this.classification = classification; }


    public String getClassification() { return classification; }
    public String getUserId() { return userId; }

    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setUserId(String userId) { this.userId = userId; }
}
