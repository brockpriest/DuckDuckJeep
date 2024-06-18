package edu.weber.cs.w01353438.duckduckjeep.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.weber.cs.w01353438.duckduckjeep.db.Models.Duck;

@Database(entities = Duck.class, version = 1)
public abstract class AppDatabase extends RoomDatabase{

    private static AppDatabase db;

    public static AppDatabase getInstance(Context context){
        if(db != null){
            return db;
        }

        db = Room.databaseBuilder(context, AppDatabase.class,"duckDatabase").build();

        return db;
    }

    public abstract DuckDAO getDuckDAO();

}
