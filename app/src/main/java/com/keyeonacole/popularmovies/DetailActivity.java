package com.keyeonacole.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.keyeonacole.popularmovies.database.MovieDataEntry;
import com.keyeonacole.popularmovies.database.movieDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by keyeona on 5/13/18.
 */

public class DetailActivity extends AppCompatActivity implements ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnChildClickListener {
    private String mMyMovieID = new String();
    private String mMyPoster = new String();
    private String mMyMovieRelease = new String();
    private String mMyMovieOverview = new String();
    private String mMyMovieVoteAverage = new String();
    private String mMyMovieTitle = new String();
    private String mMyMovieTrailerKey = new String();

    private Boolean mMyFavorite;

    private movieDatabase mdb;

    private expandableListViewAdapter reviewsListViewAdapter;
    private List<String> listDataHeader = new ArrayList<>();
    private List<String> listDataItem = new ArrayList<String>();
    private HashMap<String,List<String>> listHashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent dataIntent = getIntent();
        final Bundle dataBundle = dataIntent.getExtras();
        mdb = movieDatabase.getInstance(getApplicationContext());
        //final UserModel viewModel = ViewModelProviders.of(this).get(UserModel.class);



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
                    Toast.makeText(DetailActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    mMyMovieID = dataBundle.getString("MovieID");
                    mdb = movieDatabase.getInstance(getApplicationContext());
                    final Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            mdb.MovieDao().deleteFav(mMyMovieID);
                        }
                    };
                    new executeDB().execute(r);
                }
            }
        });
        //The API call return Reviews for a movie
        new getReview().execute();
        ExpandableListView expandableListView = findViewById(R.id.Reviews);
        expandableListView.setOnGroupExpandListener(this);
        expandableListView.setOnGroupCollapseListener(this);
        expandableListView.setOnChildClickListener(this);
        reviewsListViewAdapter = new expandableListViewAdapter(DetailActivity.this,listDataHeader,listHashMap);
        expandableListView.setAdapter(reviewsListViewAdapter);

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageButtonClicked(dataBundle);
            }
        });
    }

    //the request to you tube using the key http://youtube.com/watch?v=YOUTUBE_KEY
    public void populateUI(Bundle dataBundle){

        if(dataBundle != null){
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
                if(youtubeKey != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtubeKey));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(DetailActivity.this, "No Trailer for this movie is available", Toast.LENGTH_SHORT).show();
                }
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
                LiveData<List<MovieDataEntry>> favoriteMovies = (LiveData<List<MovieDataEntry>>) mdb.MovieDao().getAll();
                favoriteMovies.observe(DetailActivity.this, new Observer<List<MovieDataEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<MovieDataEntry> movieDataEntries) {

                    }
                });

                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGroupExpand(int groupPosition) {

    }

    @Override
    public void onGroupCollapse(int groupPosition) {

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    public class getReview extends AsyncTask<URL, Void, String> implements ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnChildClickListener {

        @Override
        protected String doInBackground(URL... urls) {
            final String API_KEY = getResources().getString(R.string.theMovieDbKey);
            final String API_BASE= getResources().getString(R.string.apiBase);
            final String noReviews = getResources().getString(R.string.noAvailableReview);
            listHashMap.clear();
            listDataHeader.clear();
            listDataItem.clear();



            JSONObject reviewObj = null;
            try {
                URL movieReviewUrl = new URL(API_BASE + mMyMovieID + "/reviews?api_key=" + API_KEY);
                reviewObj = jsonInteractions.DataFromUrl(movieReviewUrl);
                JSONArray reviewArray = reviewObj.getJSONArray("results");
                List<String> eachList = new ArrayList<>();
                if (reviewArray.length() < 1){
                    reviewArray.put(0, noReviews);
                }else{
                    for (int i = 0; i < reviewArray.length() && i < 6 ; ++i) {
                        String authorName = reviewArray.getJSONObject(i).getString("author");
                        String reviewContent = reviewArray.getJSONObject(i).getString("content");
                        listDataItem.add(reviewContent);
                        listDataHeader.add(authorName);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < listDataHeader.size(); ++i){
                 List<String> eachList = new ArrayList<>();
                 eachList.add(listDataItem.get(i));
                 listHashMap.put(listDataHeader.get(i), eachList);
            }

            return null;
        }

        @Override
        public void onGroupExpand(int groupPosition) {
            Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + "List Expanded.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onGroupCollapse(int groupPosition) {
            Toast.makeText(getApplicationContext(),
                    listDataHeader.get(groupPosition) + "List Collapsed.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " -> "
            + listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_LONG).show();
            return false;
        }
    }
}

