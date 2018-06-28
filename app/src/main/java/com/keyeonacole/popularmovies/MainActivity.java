package com.keyeonacole.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    ProgressBar mProgressBar;
    private List<String> mMyPosterList= new ArrayList<>();
    private List<String> mMyMovieReleaseList= new ArrayList<>();
    private List<String> mMyMovieOverviewList= new ArrayList<>();
    private List<String> mMyMovieVoteAverageList= new ArrayList<>();
    private List<String> mMyMovieTitleList= new ArrayList<>();
    private List<String> mMyMovieTrailerKeyList= new ArrayList<>();

    private movieDatabase mdb;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mdb = movieDatabase.getInstance(getApplicationContext());
        //Maybe I should add a check to see if the API string is the same...

        // Some interesting information to think implementing from the guide ;) Guess which one
        // Here is a hint: https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true
        // You can use Picasso to easily load album art thumbnails into your views using:
        //
        // Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);
        //
        // Picasso will handle loading the images on a background thread, image decompression and caching the images.
        //http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]

        //IMPORTANT: PLEASE REMOVE YOUR API KEY WHEN SHARING CODE PUBLICALLY
        //Please use the string theMovieDbKey


        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

            //Spinner listener
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                //Get the Sort by string
                String state = null;

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (isNetworkAvailable()){
                     //Clear the lists
                        mMyPosterList.clear();
                        mMyMovieReleaseList.clear();
                        mMyMovieOverviewList.clear();
                        mMyMovieVoteAverageList.clear();
                         mMyMovieTitleList.clear();
                         mMyMovieTrailerKeyList.clear();
                        Integer text = spinner.getSelectedItemPosition();
                        System.out.println(text);
                         if (text == 0 ){
                            state = "rating";
                             new getMovies().execute(formURL(state));
                        } else if (text == 1){
                            state = "popularity";
                           new getMovies().execute(formURL(state));
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Please Connect to the internet" ,
                                Toast.LENGTH_LONG).show();
                    }
             }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    new getMovies().execute(formURL(state));
            }
            });
            //OK if the internet option is turned off but does not handle timeouts
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public URL formURL(String spinnerState){
        final String API_KEY = getResources().getString(R.string.theMovieDbKey);

        final String API_BASERating = getResources().getString(R.string.apiCallBaseRating);
        final String API_BASEPopularity = getResources().getString(R.string.apiCallBasePopularity);


        System.out.println(spinnerState);
        URL url = null;
        try {
            if (spinnerState == "popularity"){
                url = new URL(API_BASEPopularity + "api_key=" + API_KEY + "&language=en-US&page=1");
            }else if (spinnerState == "rating"){
                url = new URL(API_BASERating + "api_key=" + API_KEY + "&language=en-US&page=1");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
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
            //https://api.themoviedb.org/3/discover/movie?api_key=<<api_key>>&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1
            //http://api.themoviedb.org/3/movie/157336/videos?api_key=###
            try {
                JSONObject moviesObj = jsonDataFromUrl(voids[0]);
                JSONArray moviesArray = moviesObj.getJSONArray("results");
                System.out.println(moviesObj);
                final String API_KEY = getResources().getString(R.string.theMovieDbKey);


                for (int i = 0; i < moviesArray.length(); ++i) {
                    String PosterUrlSuffix = parseJsonData(moviesArray, "poster_path", i);
                    String combined = new String("http://image.tmdb.org/t/p/w780/" + PosterUrlSuffix);

                    mMyPosterList.add(combined);
                    String myMovieReleaseDate = parseJsonData(moviesArray, "release_date", i);
                    mMyMovieReleaseList.add(myMovieReleaseDate);
                    String myMovieOverview = parseJsonData(moviesArray, "overview", i);
                    mMyMovieOverviewList.add(myMovieOverview);
                    String myMovieVoteAverage = parseJsonData(moviesArray, "vote_average", i);
                    mMyMovieVoteAverageList.add(myMovieVoteAverage);
                    String myMovieTitle = parseJsonData(moviesArray, "title", i);
                    mMyMovieTitleList.add(myMovieTitle);
                    String movieID = parseJsonData(moviesArray, "id", i);
                    URL movieTrailers = new URL("http://api.themoviedb.org/3/movie/" + movieID + "/videos?api_key="+ API_KEY);
                    JSONObject trailersObj = jsonDataFromUrl(movieTrailers);
                    JSONArray trailersArrays = trailersObj.getJSONArray("results");
                    String trailerKey = trailersArrays.getJSONObject(0).getString("key");
                    mMyMovieTrailerKeyList.add(trailerKey);

                    //String youtubeID = trailersArrays;
                    System.out.println(
                            trailerKey
                    );

                    Boolean favoriteStatus = new Boolean("false");



                    System.out.println(movieTrailers);

                    //MovieDataEntry movie = new MovieDataEntry(movieID, combined, myMovieReleaseDate, myMovieOverview, myMovieVoteAverage, myMovieTitle, movieTrailers, favoriteStatus );
                    //mdb.MovieDao().insertAll(movie);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return String.valueOf(mMyPosterList);
        }


        public JSONObject jsonDataFromUrl(URL movieCall) throws IOException, JSONException {
            try {
                URL apiCall = movieCall;
                HttpURLConnection urlConnection = (HttpURLConnection) apiCall.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String jsonData = stringBuilder.toString();
                urlConnection.disconnect();
                return new JSONObject(jsonData);
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        public String parseJsonData(JSONArray jsonData, String filter, Integer i) {
            if (jsonData != null) {
                try {
                    JSONObject movieDataOBJ = jsonData.getJSONObject(i);
                    String movieData = movieDataOBJ.getString(filter);
                    System.out.println(movieData);
                    return movieData;
                } catch (JSONException e) {
                    Log.e("JSON Exception", e.getMessage(), e);
                }
            }
            return null;
        }




        @Override
        protected void onPostExecute(String mMyposterList) {

            if (mMyPosterList == null) {
                mMyposterList = "THERE WAS AN ERROR: the data returned null";

            }
            GridView gridview = findViewById(R.id.gridview);
            ArrayList<String> al = (ArrayList<String>) mMyPosterList;
            //call the image adapter
            gridview.setAdapter(new gridViewAdapter(getApplicationContext(), al));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    toNextPage.putExtras(bundle);
                    startActivity(toNextPage);
                    Toast.makeText(MainActivity.this, "" + position,
                            Toast.LENGTH_SHORT).show();
                }
            });

            mProgressBar.setVisibility(View.GONE);
            Log.i("INFO", mMyposterList);
        }
    }
}
