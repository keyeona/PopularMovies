package com.keyeonacole.popularmovies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.keyeonacole.popularmovies.database.MovieDataEntry;

import java.util.List;

@Dao
public interface movieDao {
    @Query("SELECT * FROM movieTable")
    List<MovieDataEntry> getAll();

    @Query("SELECT * FROM movieTable WHERE favorite LIKE :status")
    List<MovieDataEntry> loadAllByFavorites(Boolean status);

    @Query("UPDATE movieTable set favorite = :status WHERE movie_id = :movieID ")
    void updateFavorite(Boolean status, String movieID);

    @Insert
    void insertAll(MovieDataEntry... movieDataEntries);

    @Delete
    void delete(MovieDataEntry movieDataEntry);

    @Update
    void update(MovieDataEntry movieDataEntry);
}