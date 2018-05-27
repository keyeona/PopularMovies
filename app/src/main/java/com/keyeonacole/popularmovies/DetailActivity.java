package com.keyeonacole.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by keyeona on 5/13/18.
 */

class DetailActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent dataIntent = getIntent();
        Bundle dataBundle = dataIntent.getExtras();
        System.out.println(dataBundle);
        populateUI(dataBundle);
    }

    public void populateUI(Bundle dataBundle){
        if(dataBundle!= null){
            setContentView(R.layout.detail_view);
            Boolean validTitle = dataBundle.containsKey("MovieTitle");
            if (validTitle){
                TextView titleTV = findViewById(R.id.title);
                titleTV.setText(dataBundle.getString("MovieTitle"));
            }
            Boolean validRelease = dataBundle.containsKey("MovieRelease");
            if (validRelease){
                TextView releaseTV = findViewById(R.id.releaseDate);
                releaseTV.setText(dataBundle.getString("MovieRelease"));
            }
            Boolean validDescription = dataBundle.containsKey("MovieDescription");
            if (validDescription){
                TextView descriptionTV = findViewById(R.id.description);
                descriptionTV.setText(dataBundle.getString("MovieDescription"));
            }
            Boolean validMovieRating = dataBundle.containsKey("MovieVoteAverage");
            if (validMovieRating){
                TextView descriptionTV = findViewById(R.id.rating);
                descriptionTV.setText(dataBundle.getString("MovieVoteAverage"));
            }
            Boolean validPoster = dataBundle.containsKey("MoviePoster");
            if (validPoster){
                Picasso.with(getApplicationContext()).load(dataBundle.getString("MoviePoster")).into((ImageView) findViewById(R.id.posterView));
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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
