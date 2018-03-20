package com.example.android.popularmovies.utilities;

import android.net.Uri;

import com.example.android.popularmovies.data.PopularMoviesPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the themoviedb.org server
 */
public class NetworkUtils {

    /**
     * Builds the URL used to query themoviedb.org.
     *
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl() {

        /*Uri builtUri = Uri.parse(THEMOVIEDB_API_URL).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();*/

        String moviesQueryUrl = PopularMoviesPreferences.getThemoviedbMoviesUrl();

        Uri builtUri = Uri.parse(moviesQueryUrl).buildUpon().build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildPosterUrl(String posterPath) {

/*        Uri builtUri = Uri.parse(THEMOVIEDB_POSTER_URL).buildUpon()
                .appendEncodedPath(POSTER_SIZE)
                .appendEncodedPath(posterPath)
                .build();*/

        String posterQueryUrl = PopularMoviesPreferences.getThemoviedbPosterUrl(posterPath);

        Uri builtUri = Uri.parse(posterQueryUrl).buildUpon().build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
