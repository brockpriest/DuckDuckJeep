package edu.weber.cs.w01353438.duckduckjeep.db.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.type.DateTime;

import java.util.Date;

@Entity
public class Duck {
    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDuckId() {
        return duckId;
    }

    public void setDuckId(String duckId) {
        this.duckId = duckId;
    }

    public String getDuckLastLocation() {
        return duckLastLocation;
    }

    public void setDuckLastLocation(String duckLastLocation) {
        this.duckLastLocation = duckLastLocation;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int primaryKey;

    @ColumnInfo(name = "duckId")
    private String duckId;

    @ColumnInfo(name = "lastLocation")
    private String duckLastLocation;
}
