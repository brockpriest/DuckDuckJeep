package edu.weber.cs.w01353438.duckduckjeep.db.CloudDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

@Dao
public interface cloudDuckDAO {
    @Insert
    void addDuck(cloudDuck duck);

    @Delete
    void deleteDuck(cloudDuck duck);

    @Update
    void updateDuck(cloudDuck duck);
}
