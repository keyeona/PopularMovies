package com.keyeonacole.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


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

       //Get the spinner current state


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            final String[] stateArray = getResources().getStringArray(R.array.sortBY_api);
            String state = null;

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
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
        

    }

    public URL formURL(String spinnerState){

        final String API_KEY = getResources().getString(R.string.theMovieDbKey);
        final String API_BASE = getResources().getString(R.string.apiCallBase);
        final String API_SORT= spinnerState;
        System.out.println(spinnerState);

        //Building the inital API URL and then attempt to connect. Might not be the best way need to sort to get picture id.
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
            //Sort by Options: original_title.asc original_title.des vote_average.asc vote_average.desc
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
