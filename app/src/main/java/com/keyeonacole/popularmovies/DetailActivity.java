package com.keyeonacole.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

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

        populatUI(dataBundle);


    }

    public void populatUI(Bundle dataBundle){

        if(dataBundle!= null){
            setContentView(R.layout.detail_view);
            Boolean validTitle = dataBundle.containsKey("MovieTitle");
            System.out.println(validTitle);
            if (validTitle){
                TextView titleTV = findViewById(R.id.title);
                titleTV.setText(dataBundle.getString("MovieTitle"));
            }
            Boolean validID = dataBundle.containsKey("MovieId");
            if (validID){
                //setContentView(R.layout.detail_view);
                TextView titleTV = findViewById(R.id.voteCount);
                titleTV.setText(dataBundle.getString("MovieId"));
            }
            Boolean validDescritpion = dataBundle.containsKey("MovieDescription");
            if (validDescritpion){
                //setContentView(R.layout.detail_view);
                TextView titleTV = findViewById(R.id.description);
                titleTV.setText(dataBundle.getString("MovieDescription"));
            }
            Boolean validAverage = dataBundle.containsKey("MoviePopularity");
            if (validAverage){
                //setContentView(R.layout.detail_view);
                TextView titleTV = findViewById(R.id.rating);
                titleTV.setText(dataBundle.getString("MoviePopularity"));
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
