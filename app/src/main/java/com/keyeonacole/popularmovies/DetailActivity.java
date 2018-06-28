package com.keyeonacole.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.security.PublicKey;

/**
 * Created by keyeona on 5/13/18.
 */

class DetailActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent dataIntent = getIntent();
        final Bundle dataBundle = dataIntent.getExtras();
        System.out.println(dataBundle);
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

                }
                else {
                    System.out.println(isChecked);
                    Toast.makeText(DetailActivity.this, " Removed from Favorites", Toast.LENGTH_SHORT).show();


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

