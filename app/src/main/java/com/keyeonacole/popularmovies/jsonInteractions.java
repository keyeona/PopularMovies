package com.keyeonacole.popularmovies;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by keyeona on 7/3/18.
 */

public class jsonInteractions {

    public static JSONObject DataFromUrl(URL movieCall) throws IOException, JSONException {
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


    public static String parseData(JSONArray jsonData, String filter, Integer i) {
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
}
