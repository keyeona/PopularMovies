package com.keyeonacole.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

/**
 * Created by keyeona on 6/20/18.
 */

@Database(entities = {MovieDataEntry.class}, version = 2, exportSchema = false)
public abstract  class movieDatabase extends RoomDatabase{
    private static final String LOG_TAG = movieDatabase.class.getSimpleName();
    private  static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movies";
    private static  movieDatabase sInstance;

    public static movieDatabase getInstance(Context context){
        if (sInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG, "Creating Database");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        movieDatabase.class, movieDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting Database Instance");
        return sInstance;
    }
       public abstract movieDao MovieDao();

}
