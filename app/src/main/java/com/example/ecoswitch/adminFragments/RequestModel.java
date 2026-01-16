package com.example.ecoswitch.adminFragments;


import com.example.ecoswitch.userFiles.User;
import com.example.ecoswitch.userFragments.Photo;

public class RequestModel {
    private User user;
    private Photo photo;

    public RequestModel(User user, Photo photo) {
        this.user = user;
        this.photo = photo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
