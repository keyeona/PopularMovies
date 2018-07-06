package com.keyeonacole.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keyeona on 5/14/18.
 */

public class gridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mUrls = new ArrayList<String>();

    public gridViewAdapter(Context getMovies, ArrayList<String> al) {
        mContext = getMovies;
        mUrls = al;
    }

    @Override
    public int getCount() {
        return  mUrls.size();
    }

    @Override
    public String getItem(int i) {
        return mUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {


        // Get the image URL for the current position.
        String url = getItem(i);
        ImageView view = null;

        if (convertView == null) {
            view = new ImageView(mContext);
            view.setLayoutParams(new GridView.LayoutParams(600, 600));
            view.setPadding(10, 10, 10, 10);
        } else {
            view = (ImageView) convertView;
        }

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext) //
                .load(url) //
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fit()
                .centerInside()
                .tag(mContext)
                .into(view);

        return view;
    }
}
