package com.keyeonacole.popularmovies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.keyeonacole.popularmovies.database.MovieDataEntry;

import java.util.List;

@Dao
public interface movieDao {
    @Query("SELECT * FROM movieTable")
    List<MovieDataEntry> getAll();

    // These are just example until I figure out what exactly to do with theses
    //TODO Remove Commented out code

    //@Query("SELECT * FROM user WHERE uid IN (:userIds)")
    //List<MovieDataEntry> loadAllByIds(int[] userIds);

    //@Query("SELECT * FROM user WHERE first_name LIKE :first AND "
      //      + "last_name LIKE :last LIMIT 1")
    //MovieDataEntry findByName(String first, String last);

    @Insert
    void insertAll(MovieDataEntry... movieDataEntries);

    @Delete
    void delete(MovieDataEntry movieDataEntry);
}