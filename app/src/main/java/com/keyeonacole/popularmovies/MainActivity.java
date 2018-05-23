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



public class MainActivity extends AppCompatActivity {
    ProgressBar mProgressBar;
    ArrayList<String> mMyPosterList = new ArrayList<String>();
    ArrayList<String> mMyMovieIdList = new ArrayList<String>();
    ArrayList<String> mMyMovieOverviewList = new ArrayList<String>();
    ArrayList<String> mMyMovieVoteAverageList = new ArrayList<String>();
    ArrayList<String> mMyMovieTitleList = new ArrayList<String>();
    ArrayList<String> mMyMoviePopularityList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        if (isNetworkAvailable()){
            //Spinner listener
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                //Get the Sort by string
                final String[] stateArray = getResources().getStringArray(R.array.sortBY_api);
                String state = null;

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    //Clear the lists
                     mMyPosterList.clear();
                     mMyMovieIdList.clear();
                     mMyMovieOverviewList.clear();
                     mMyMovieVoteAverageList.clear();
                     mMyMovieTitleList.clear();
                     mMyMoviePopularityList.clear();
                    Integer text = spinner.getSelectedItemPosition();
                 if (text == 0 ){
                        state = stateArray[0];
                        new getMovies().execute(formURL(state));
                    } else if (text == 1){
                        state = stateArray[1];
                        new getMovies().execute(formURL(state));
                    } else  if (text == 2){
                        state = stateArray[2];
                        new getMovies().execute(formURL(state));
                    }
             }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    new getMovies().execute(formURL(stateArray[0]));
            }
            });

        } else{
            //OK if the internet option is turned off but does not handle timeouts
            Toast.makeText(MainActivity.this, "Please Connect to the internet" ,
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public URL formURL(String spinnerState){

        final String API_KEY = getResources().getString(R.string.theMovieDbKey);
        final String API_BASE = getResources().getString(R.string.apiCallBase);
        final String API_SORT= spinnerState;
        System.out.println(spinnerState);

        //Building the inital API URL and then attempt to connect. Might not be the best way need to sort to get picture id.
        //Adding Trailer Soon!
        URL url = null;
        try {
            url = new URL(API_BASE + "api_key=" + API_KEY + "&language=en-US&sort_by=" + API_SORT + "&include_adult=false&include_video=false&page=1");

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
            //Sort by Options: popularity.asc popularity.des vote_average.desc
            try {
                JSONObject moviesObj = jsonDataFromUrl(voids[0]);
                JSONArray moviesArray = moviesObj.getJSONArray("results");
                System.out.println(moviesObj);



                for (int i = 0; i < moviesArray.length(); ++i) {
                    String PosterUrlSuffix = parseJsonData(moviesArray, "poster_path", i);
                    String combined = new String ("http://image.tmdb.org/t/p/w780/" + PosterUrlSuffix);
                    mMyPosterList.add(combined);

                    String myMovieID = parseJsonData(moviesArray, "id", i);
                    mMyMovieIdList.add(myMovieID);
                    String myMovieOverview = parseJsonData(moviesArray, "overview", i);
                    mMyMovieOverviewList.add(myMovieOverview);
                    String myMovieVoteAverage = parseJsonData(moviesArray, "vote_average", i);
                    mMyMovieVoteAverageList.add(myMovieVoteAverage);
                    String myMovieTitle = parseJsonData(moviesArray, "title", i);
                    mMyMovieTitleList.add(myMovieTitle);
                    String myMoviePopularity = parseJsonData(moviesArray, "popularity", i);
                    mMyMoviePopularityList.add(myMoviePopularity);

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
            if(mMyPosterList == null) {
                mMyposterList = "THERE WAS AN ERROR: the data returned null";

            }
            GridView gridview = findViewById(R.id.gridview);

            ArrayList<String> al = mMyPosterList;


            //call the image adapter
            gridview.setAdapter(new gridViewAdapter(getApplicationContext(), al));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Intent toNextPage = new Intent(MainActivity.this,
                            DetailActivity.class);
                    Bundle bundle = new Bundle();
                    //I need to add some error checks for null objects
                    bundle.putString("MovieId", mMyMovieIdList.get(position));
                    bundle.putString("MovieTitle", mMyMovieTitleList.get(position).toString());
                    bundle.putString("MovieDescription", mMyMovieOverviewList.get(position));
                    bundle.putString("MovieVoteAverage", mMyMovieVoteAverageList.get(position));
                    bundle.putString("MoviePopularity", mMyMoviePopularityList.get(position));
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
