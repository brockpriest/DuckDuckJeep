package edu.weber.cs.w01353438.duckduckjeep.db.CloudDB;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class cloudDuck {
    private String duckId;
    private String duckName;
    private GeoPoint duckLastLocation;
    private String CreatedBy;
    private Date CreatedAt;

    public String getDuckId() {
        return duckId;
    }

    public void setDuckId(String duckId) {
        this.duckId = duckId;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        this.CreatedBy = createdBy;
    }

    public Date getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.CreatedAt = createdAt;
    }

    public GeoPoint getDuckLastLocation() {
        return duckLastLocation;
    }

    public void setDuckLastLocation(GeoPoint duckLastLocation) {
        this.duckLastLocation = duckLastLocation;
    }

    public String getDuckName() {
        return duckName;
    }

    public void setDuckName(String duckName) {
        this.duckName = duckName;
    }
}
