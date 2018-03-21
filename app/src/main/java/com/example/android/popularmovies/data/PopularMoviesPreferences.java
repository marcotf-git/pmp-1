package com.example.android.popularmovies.data;

import android.util.Log;


/**
 * This class will store the main application preferences and url references.
 */
public class PopularMoviesPreferences {


    //private static final String API_KEY = "API_KEY_HERE";
    private static final String API_KEY = "MY_MOVIE_DB_API_KEY";

    private static final String THEMOVIEDB_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String PATH_POPULAR_MOVIES = "movie/popular";
    private static final String PATH_TOP_RATED_MOVIES = "movie/top_rated";
    private static String moviesQueryOption = "popular";

    private static final String THEMOVIEDB_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String DEFAULT_POSTER_SIZE = "w185";

    private static final String TAG = PopularMoviesPreferences.class.getSimpleName();


    public static void setMoviesQueryOption(String option){
         moviesQueryOption = option;
         Log.v(TAG, "setting movies query option:" + moviesQueryOption);
    }

    public static String getThemoviedbMoviesUrl() {

        switch (moviesQueryOption) {
            case "popular":
                return (THEMOVIEDB_BASE_URL + PATH_POPULAR_MOVIES+ "?api_key=" + API_KEY);
            case "top_rated":
                return (THEMOVIEDB_BASE_URL + PATH_TOP_RATED_MOVIES + "?api_key=" + API_KEY);
        }
        return null;
    }


    public static String getThemoviedbPosterUrl(String posterPath) {
        return (THEMOVIEDB_POSTER_URL + DEFAULT_POSTER_SIZE + "/" + posterPath);
    }

    public static String getMoviesQueryOption() {
        return moviesQueryOption;
    }

}
