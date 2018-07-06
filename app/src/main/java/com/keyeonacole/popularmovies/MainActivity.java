package com.keyeonacole.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.keyeonacole.popularmovies.database.MovieDataEntry;
import com.keyeonacole.popularmovies.database.movieDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    ProgressBar mProgressBar;
    private List<String> mMyMovieIds= new ArrayList<>();
    private List<String> mMyPosterList= new ArrayList<>();
    private List<String> mMyMovieReleaseList= new ArrayList<>();
    private List<String> mMyMovieOverviewList= new ArrayList<>();
    private List<String> mMyMovieVoteAverageList= new ArrayList<>();
    private List<String> mMyMovieTitleList= new ArrayList<>();
    private List<String> mMyMovieTrailerKeyList= new ArrayList<>();
    private GridView mGridView;



    private movieDatabase mdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = findViewById(R.id.gridview);

        if (savedInstanceState != null) {
            // Then the application is being reloaded
            Toast.makeText(getApplicationContext(),  "This is a nightmare.I hate my life.",Toast.LENGTH_SHORT).show();

        }


        mdb = movieDatabase.getInstance(getApplicationContext());
        // Picasso will handle loading the images on a background thread, image decompression and caching the images.
        //http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
        //Please use the string theMovieDbKey
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



            //Spinner listener
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                //Get the Sort by string
                String state = null;



                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    final String noConnection = getResources().getString(R.string.noInternetConnection);

                    if (isNetworkAvailable()){
                        Integer text = spinner.getSelectedItemPosition();
                         if (text == 0 ){
                             clearList();
                             state = "rating";
                             new getMovies().execute(formURL(state));
                        } else if (text == 1){
                             clearList();
                             state = "popularity";
                             new getMovies().execute(formURL(state));
                        } else if (text == 2){
                            clearList();
                             mProgressBar = findViewById(R.id.progressBar);
                             mProgressBar.setVisibility(View.VISIBLE);
                             mdb = movieDatabase.getInstance(getApplicationContext());

                             LiveData<List<MovieDataEntry>> favoriteMovies = mdb.MovieDao().getAll();
                             favoriteMovies.observe(MainActivity.this, new Observer<List<MovieDataEntry>>() {
                                 @Override
                                 public void onChanged(@Nullable List<MovieDataEntry> movieDataEntries) {
                                     if(spinner.getSelectedItemPosition() == 2) {
                                         clearList();

                                     Integer fto = movieDataEntries.size();
                                     if (fto > 0){
                                         for (int i = 0; i < fto ; ++i) {
                                             String myMovieTitle = movieDataEntries.get(i).getMyMovieTitle();
                                             mMyMovieTitleList.add(myMovieTitle);

                                             String moviePoster = movieDataEntries.get(i).getMovieUrl();
                                             mMyPosterList.add(moviePoster);

                                             String myMovieReleaseDate = movieDataEntries.get(i).getMyMovieReleaseDate();
                                             mMyMovieReleaseList.add(myMovieReleaseDate);

                                             String myMovieOverview = movieDataEntries.get(i).getMyMovieOverview();
                                             mMyMovieOverviewList.add(myMovieOverview);

                                             String myMovieVoteAverage = movieDataEntries.get(i).getMyMovieVoteAverage();
                                             mMyMovieVoteAverageList.add(myMovieVoteAverage);

                                             String movieID = movieDataEntries.get(i).getMyMovieID();
                                             mMyMovieIds.add(movieID);

                                             String trailerKey = movieDataEntries.get(i).getMyTrailer();
                                             mMyMovieTrailerKeyList.add(trailerKey);
                                         }

                                         }
                                     }
                                     new getFavorites().favoritesUi();

                                 }
                             });
                             mProgressBar.setVisibility(View.GONE);
                         }
                    }else{
                        Toast.makeText(MainActivity.this, noConnection ,
                                Toast.LENGTH_LONG).show();
                    }
             }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    new getMovies().execute(formURL(state));
            }
            });


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        int lastView = mGridView.getLastVisiblePosition();
        int firstView = mGridView.getFirstVisiblePosition();
        outState.putInt("LAST_VISIBLE", lastView);
        outState.putInt("FIRST_VISIBLE", firstView);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mGridView.setSelection(savedInstanceState.getInt("LAST_VISIBLE"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public URL formURL(String spinnerState){
        final String API_KEY = getResources().getString(R.string.theMovieDbKey);
        final String API_BASE = getResources().getString(R.string.apiBase);
        final String API_Rating = getResources().getString(R.string.Rating);
        final String API_Popularity = getResources().getString(R.string.Popularity);

        URL url = null;
        try {
            if (spinnerState == "popularity"){
                url = new URL(API_BASE + API_Popularity + "api_key=" + API_KEY + "&language=en-US&page=1");
            }else if (spinnerState == "rating"){
                url = new URL(API_BASE + API_Rating + "api_key=" + API_KEY + "&language=en-US&page=1");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    public void clearList(){
        mMyPosterList.clear();
        mMyMovieReleaseList.clear();
        mMyMovieOverviewList.clear();
        mMyMovieVoteAverageList.clear();
        mMyMovieTitleList.clear();
        mMyMovieTrailerKeyList.clear();
        mMyMovieIds.clear();
    }

    public class getMovies extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            mProgressBar = findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... voids) {
            //http://api.themoviedb.org/3/movie/157336/videos?api_key=###
            final String API_BASE= getResources().getString(R.string.apiBase);
            final String noReviews = getResources().getString(R.string.noAvailableReview);

            String url_check = String.valueOf(voids[0]);
            try {
                JSONArray moviesArray = null;
                if (url_check != "skip") {
                    JSONObject moviesObj = jsonInteractions.DataFromUrl(voids[0]);
                    moviesArray = moviesObj.getJSONArray("results");
                    final String API_KEY = getResources().getString(R.string.theMovieDbKey);

                    for (int i = 0; i < moviesArray.length(); ++i) {
                        String PosterUrlSuffix = jsonInteractions.parseData(moviesArray, "poster_path", i);
                        String combined = new String("http://image.tmdb.org/t/p/w780/" + PosterUrlSuffix);

                        mMyPosterList.add(combined);
                        String myMovieReleaseDate = jsonInteractions.parseData(moviesArray, "release_date", i);
                        mMyMovieReleaseList.add(myMovieReleaseDate);
                        String myMovieOverview = jsonInteractions.parseData(moviesArray, "overview", i);
                        mMyMovieOverviewList.add(myMovieOverview);
                        String myMovieVoteAverage = jsonInteractions.parseData(moviesArray, "vote_average", i);
                        mMyMovieVoteAverageList.add(myMovieVoteAverage);
                        String myMovieTitle =jsonInteractions.parseData(moviesArray, "title", i);
                        mMyMovieTitleList.add(myMovieTitle);
                        //Get the movie ID to use for the trailer URL
                        String movieID = jsonInteractions.parseData(moviesArray, "id", i);
                        mMyMovieIds.add(movieID);
                        //The API call returns lots of DATA but we only need the first youtube key
                        URL movieTrailers = new URL(API_BASE + movieID + "/videos?api_key=" + API_KEY);
                        JSONObject trailersObj = jsonInteractions.DataFromUrl(movieTrailers);
                        JSONArray trailersArrays = trailersObj.getJSONArray("results");
                        String trailerKey = trailersArrays.getJSONObject(0).getString("key");
                        mMyMovieTrailerKeyList.add(trailerKey);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return String.valueOf(mMyPosterList);
        }

        @Override
        protected void onPostExecute(String mMyposterList) {

            if (mMyPosterList == null) {
                mMyposterList = "THERE WAS AN ERROR: the data returned null";

            }
            ArrayList<String> al = (ArrayList<String>) mMyPosterList;
            //call the image adapter
            mGridView.setAdapter(new gridViewAdapter(getApplicationContext(), al));
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Intent toNextPage = new Intent(MainActivity.this,
                            DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("MovieRelease", mMyMovieReleaseList.get(position));
                    bundle.putString("MovieTitle", mMyMovieTitleList.get(position));
                    bundle.putString("MovieDescription", mMyMovieOverviewList.get(position));
                    bundle.putString("MovieVoteAverage", mMyMovieVoteAverageList.get(position));
                    bundle.putString("MoviePoster", mMyPosterList.get(position));
                    bundle.putString("MovieTrailerKey", mMyMovieTrailerKeyList.get(position));
                    bundle.putString("MovieID", mMyMovieIds.get(position));
                    toNextPage.putExtras(bundle);
                    startActivity(toNextPage);

                }
            });

            mProgressBar.setVisibility(View.GONE);
            Log.i("INFO", mMyposterList);
        }
    }

    public class getFavorites {


        protected void favoritesUi(){


            ArrayList<String> favorites = (ArrayList<String>) mMyPosterList;
            //call the image adapter
            mGridView.setAdapter(new gridViewAdapter(getApplicationContext(), favorites));

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Intent toNextPage = new Intent(MainActivity.this,
                            DetailActivity.class);
                    Bundle bundle = new Bundle();
                    //I need to add some error checks for null objects
                    bundle.putString("MovieRelease", mMyMovieReleaseList.get(position));
                    bundle.putString("MovieTitle", mMyMovieTitleList.get(position));
                    bundle.putString("MovieDescription", mMyMovieOverviewList.get(position));
                    bundle.putString("MovieVoteAverage", mMyMovieVoteAverageList.get(position));
                    bundle.putString("MoviePoster", mMyPosterList.get(position));
                    bundle.putString("MovieTrailerKey", mMyMovieTrailerKeyList.get(position));
                    bundle.putString("MovieID", mMyMovieIds.get(position));
                    toNextPage.putExtras(bundle);
                    startActivity(toNextPage);

                }
            });
        }
    }
}
