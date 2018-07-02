package com.keyeonacole.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.keyeonacole.popularmovies.database.MovieDataEntry;
import com.keyeonacole.popularmovies.database.movieDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by keyeona on 5/13/18.
 */

class DetailActivity extends AppCompatActivity{
    private String mMyMovieID = new String();
    private String mMyPoster = new String();
    private String mMyMovieRelease = new String();
    private String mMyMovieOverview = new String();
    private String mMyMovieVoteAverage = new String();
    private String mMyMovieTitle = new String();
    private String mMyMovieTrailerKey = new String();
    private Boolean mMyFavorite;


    private movieDatabase mdb;

    DetailActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Intent dataIntent = getIntent();
        final Bundle dataBundle = dataIntent.getExtras();
        System.out.println(dataBundle);
        mdb = movieDatabase.getInstance(getApplicationContext());


        //Should I perform valid checks here?
        mMyMovieID = dataBundle.getString("MovieID");
        mMyMovieTitle = dataBundle.getString("MovieTitle");
        mMyMovieRelease = dataBundle.getString("MovieRelease");
        mMyPoster = dataBundle.getString("MoviePoster");
        mMyMovieOverview = dataBundle.getString("MovieDescription");
        mMyMovieVoteAverage = dataBundle.getString("MovieVoteAverage");
        mMyMovieTrailerKey = dataBundle.getString("MovieTrailerKey");

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                 Boolean dbFavorite = mdb.MovieDao().currentMovieStatus(mMyMovieID);
                 mMyFavorite = dbFavorite;
            }
        };
        new executeDB().execute(r);

        populateUI(dataBundle);
        Switch switch_button =  this.findViewById(R.id.switch1);
        ImageButton play_button = this.findViewById(R.id.play_button);

        //https://android--code.blogspot.com/2015/08/android-switch-button-listener.html
        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // If the switch button is on
                    System.out.println(isChecked);
                    Toast.makeText(DetailActivity.this, " Adding to Favorites", Toast.LENGTH_SHORT).show();
                    final Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            MovieDataEntry fav = new MovieDataEntry(mMyMovieID, mMyPoster, mMyMovieRelease, mMyMovieOverview, mMyMovieVoteAverage, mMyMovieTitle, mMyMovieTrailerKey, true);
                            mdb.MovieDao().insertAll(fav);
                        }
                    };
                    new executeDB().execute(r);
                }
                else {
                    System.out.println(isChecked);
                    Toast.makeText(DetailActivity.this, " Removed from Favorites", Toast.LENGTH_SHORT).show();
                    mMyMovieID = dataBundle.getString("MovieID");
                    mdb = movieDatabase.getInstance(getApplicationContext());
                    final Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(mMyMovieID);
                            mdb.MovieDao().deleteFav(mMyMovieID);
                            //mdb.MovieDao().nukeTable();


                        }
                    };
                    new executeDB().execute(r);
                }
            }
        });

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageButtonClicked(dataBundle);
            }
        });
    }

    //the request to you tube using the key http://youtube.com/watch?v=YOUTUBE_KEY
    public void populateUI(Bundle dataBundle){

        if(dataBundle!= null){
            setContentView(R.layout.detail_view);
            Boolean validTitle = dataBundle.containsKey("MovieTitle");
            if (validTitle){
                TextView titleTV = findViewById(R.id.title);
                titleTV.setText(mMyMovieTitle);
            }
            Boolean validRelease = dataBundle.containsKey("MovieRelease");
            if (validRelease){
                TextView releaseTV = findViewById(R.id.releaseDate);
                releaseTV.setText(mMyMovieRelease);
            }
            Boolean validDescription = dataBundle.containsKey("MovieDescription");
            if (validDescription){
                TextView descriptionTV = findViewById(R.id.description);
                descriptionTV.setText(mMyMovieOverview);
            }
            Boolean validMovieRating = dataBundle.containsKey("MovieVoteAverage");
            if (validMovieRating){
                TextView descriptionTV = findViewById(R.id.rating);
                descriptionTV.setText(mMyMovieVoteAverage);
            }
            Boolean validPoster = dataBundle.containsKey("MoviePoster");
            if (validPoster){
                Picasso.with(getApplicationContext()).load(mMyPoster).into((ImageView) findViewById(R.id.posterView));
            }if (mMyFavorite != null){
                Switch favoriteSW = findViewById(R.id.switch1);
                favoriteSW.setChecked(true);
            }else{
                Switch favoriteSW = findViewById(R.id.switch1);
                favoriteSW.setChecked(false);

            }

            //IF this movie is in DB set switch to ON else to OFF
            //MovieDataEntry fav = new MovieDataEntry(String myMovieID,String movieUrl, String myMovieReleaseDate, String myMovieOverview, String myMovieVoteAverage, String myMovieTitle, String myTrailer, Boolean myFavorite)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void onImageButtonClicked(Bundle dataBundle) {
        ImageButton trailer = findViewById(R.id.play_button);
        if(dataBundle != null){
            Boolean validMovieTrailerKey = dataBundle.containsKey("MovieTrailerKey");
            if (validMovieTrailerKey){
                String youtubeKey = dataBundle.getString("MovieTrailerKey");
              Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtubeKey));
              System.out.println(intent);
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              intent.setPackage("com.google.android.youtube");
                startActivity(intent);
        }

        }else {
            Toast.makeText(DetailActivity.this, "Unable to play Trailer, Please Try Again Later, ", Toast.LENGTH_SHORT).show();
        }
    }

    //https://stackoverflow.com/questions/36457564/display-back-button-of-action-bar-is-not-going-back-in-android
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

