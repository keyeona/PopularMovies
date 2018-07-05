package com.keyeonacole.popularmovies.database;

import android.arch.lifecycle.LiveData;
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
    LiveData<List<MovieDataEntry>> getAll();

    @Query("SELECT * FROM movieTable WHERE favorite LIKE :status")
    LiveData<List<MovieDataEntry>> loadAllByFavorites(Boolean status);

    @Query("UPDATE movieTable set favorite = :status WHERE movie_id = :movieID ")
    void updateFavorite(Boolean status, String movieID);

    @Query("SELECT favorite FROM movieTable WHERE movie_id = :movieID ")
    Boolean currentMovieStatus(String movieID);

    @Insert
    void insertAll(MovieDataEntry... movieDataEntries);

    @Query("DELETE FROM movieTable WHERE movie_id = :movieID")
    void deleteFav(String movieID);

    @Query("DELETE FROM movieTable")
    void nukeTable();

    @Update
    void update(MovieDataEntry movieDataEntry);
}