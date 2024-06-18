package edu.weber.cs.w01353438.duckduckjeep.db.Models;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class UserDuckTracking {
    private String duckId;
    private String duckName;
    private String duckLocation;
    private String duckDocumentId;
    private String userId;
    private Timestamp timestamp;

    // Constructor, getters, and setters
    public UserDuckTracking(String duckId, String duckName, String duckLocation, String duckDocumentId, Timestamp timestamp) {
        this.duckId = duckId;
        this.duckName = duckName;
        this.duckLocation = duckLocation;
        this.duckDocumentId = duckDocumentId;
        this.timestamp = timestamp;
    }
    public UserDuckTracking(){

    }

    public String getDuckId() {
        return duckId;
    }

    public void setDuckId(String duckId) {
        this.duckId = duckId;
    }

    public String getDuckName() {
        return duckName;
    }

    public void setDuckName(String duckName) {
        this.duckName = duckName;
    }

    public String getDuckLocation() {
        return duckLocation;
    }

    public void setDuckLocation(String duckLocation) {
        this.duckLocation = duckLocation;
    }

    public String getDuckDocumentId() {
        return duckDocumentId;
    }

    public void setDuckDocumentId(String duckDocumentId) {
        this.duckDocumentId = duckDocumentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

