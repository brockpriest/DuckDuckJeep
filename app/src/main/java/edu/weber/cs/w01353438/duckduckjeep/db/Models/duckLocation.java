package edu.weber.cs.w01353438.duckduckjeep.db.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class duckLocation {
    String duckId;
    GeoPoint Location;
    String state;
    String city;
    Timestamp timestamp;

    public String getDuckId() {
        return duckId;
    }

    public void setDuckId(String duckId) {
        this.duckId = duckId;
    }

    public GeoPoint getLocation() {
        return Location;
    }

    public void setLocation(GeoPoint location) {
        Location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
