package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity //<.>

public class ShortUser {
    @Id
    private UUID userID;
    private String username;
    private String email;

    public ShortUser() {
    }

    public ShortUser(UUID userID, String username, String email) {
        this.userID = userID;
        this.username = username;
        this.email = email;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }





}