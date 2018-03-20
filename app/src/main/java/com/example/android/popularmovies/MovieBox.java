package com.example.android.popularmovies;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class that has some movies titles
 */

public class MovieBox {

    private JSONArray movies;

    public MovieBox(String moviesJSONString) {

        try {
            JSONObject results = new JSONObject(moviesJSONString);
            movies = results.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfMovies() {
        return movies.length();
    }

    public JSONObject getMovieJSON(int position) {

        JSONObject movieJSON;

        try {
            movieJSON = movies.getJSONObject(position);
            return movieJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}


