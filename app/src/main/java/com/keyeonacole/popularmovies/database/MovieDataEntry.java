package com.keyeonacole.popularmovies.database;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by keyeona on 6/13/18.
 */
@Entity(tableName = "movieTable")
public class MovieDataEntry {
    @PrimaryKey(autoGenerate = true)
    private Long primaryKey;
    @ColumnInfo(name = "movie_id")
    private String myMovieID;
    @ColumnInfo(name = "movie_poster_url")
    private String movieUrl;
    @ColumnInfo(name = "movie_release_date")
    private String myMovieReleaseDate;
    @ColumnInfo(name = "movie_overview")
    private String myMovieOverview;
    @ColumnInfo(name = "movie_vote_average")
    private String myMovieVoteAverage;
    @ColumnInfo(name = "movie_title")
    private  String myMovieTitle;
    @ColumnInfo(name = "trailer_id")
    private String myTrailer;
    @ColumnInfo(name = "favorite")
    private Boolean myFavorite;


    public MovieDataEntry(String myMovieID,String movieUrl, String myMovieReleaseDate, String myMovieOverview, String myMovieVoteAverage, String myMovieTitle, String myTrailer, Boolean myFavorite) {
        this.myMovieID = myMovieID;
        this.movieUrl = movieUrl;
        this.myMovieReleaseDate = myMovieReleaseDate;
        this.myMovieOverview = myMovieOverview;
        this.myMovieVoteAverage = myMovieVoteAverage;
        this.myMovieTitle = myMovieTitle;
        this.myTrailer = myTrailer;
        this.myFavorite = myFavorite;


    }

    public Long getPrimaryKey() { return primaryKey;}

    public String getMyMovieID() { return myMovieID;}

    public String getMovieUrl(){
        return movieUrl;
    }

    public String getMyMovieReleaseDate() {
        return myMovieReleaseDate;
    }

    public String getMyMovieOverview() {
        return myMovieOverview;
    }

    public String getMyMovieTitle() {
        return myMovieTitle;
    }

    public String getMyMovieVoteAverage() {return myMovieVoteAverage;}

    public String getMyTrailer() {return myTrailer;}

    public Boolean getMyFavorite() {return myFavorite;}

    public void setPrimaryKey(Long primaryKey) { this.primaryKey = primaryKey;}

    public void setMyMovieID(String myMovieID) { this.myMovieID = myMovieID;}

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public void setMyMovieOverview(String myMovieOverview) {this.myMovieOverview = myMovieOverview; }

    public void setMyMovieReleaseDate(String myMovieReleaseDate) { this.myMovieReleaseDate = myMovieReleaseDate; }

    public void setMyMovieTitle(String myMovieTitle) {
        this.myMovieTitle = myMovieTitle;
    }

    public void setMyMovieVoteAverage(String myMovieVoteAverage) { this.myMovieVoteAverage = myMovieVoteAverage; }

    public void setMyTrailer(String myTrailer) { this.myTrailer = myTrailer; }

    public void setMyFavorite(Boolean myFavorite) { this.myFavorite = myFavorite; }

}
