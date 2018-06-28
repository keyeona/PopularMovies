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

    @Query("SELECT * FROM movieTable WHERE favorite LIKE :status")
    List<MovieDataEntry> loadAllByFavorites(Boolean status);

    @Insert
    void insertAll(MovieDataEntry... movieDataEntries);

    @Delete
    void delete(MovieDataEntry movieDataEntry);
}