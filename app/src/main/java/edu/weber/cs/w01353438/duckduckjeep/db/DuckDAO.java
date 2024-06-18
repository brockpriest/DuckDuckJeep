package edu.weber.cs.w01353438.duckduckjeep.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import edu.weber.cs.w01353438.duckduckjeep.db.Models.Duck;

@Dao
public interface DuckDAO {
    @Delete
    void deleteUserDuck(Duck duck);

    @Insert
    void addDuck(Duck duck);

    @Update
    void updateLocation(Duck duck);
}
