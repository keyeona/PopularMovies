package com.keyeonacole.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by keyeona on 5/13/18.
 */

class DetailActivity extends AppCompatActivity{
    private TextView mDisplayText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);
       mDisplayText = findViewById(R.id.title);

    }
}
