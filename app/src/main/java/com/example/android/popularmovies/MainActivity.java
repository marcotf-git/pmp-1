package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.popularmovies.data.PopularMoviesPreferences;
import com.example.android.popularmovies.utilities.NetworkUtils;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;


public class MainActivity extends AppCompatActivity
        implements GreenAdapter.ListItemClickListener {

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private GreenAdapter mAdapter;
    private RecyclerView mMoviesList;

    /*
     * If we hold a reference to our Toast, we can cancel it (if it's showing)
     * to display a new Toast. If we didn't do this, Toasts would be delayed
     * in showing up if you clicked many list items in quick succession.
     */
    private Toast mToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         *
         * We are using the GridLayoutManager.
         */
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mMoviesList.setLayoutManager(layoutManager);

         /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mMoviesList.setHasFixedSize(true);

        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new GreenAdapter(this);
        mMoviesList.setAdapter(mAdapter);

        loadMoviesData();

    }


    /**
     * This method will get the user's preferred settings, and then tell some
     * background method to get the movies data in the background.
     */
    private void loadMoviesData() {
        showMoviesDataView();
        makeThemoviedbSearchQuery();
    }

    /**
     * This method retrieves the search text from the EditText, constructs
     * the URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our (not yet created) {@link 'GithubQueryTask'}
     */
    private void makeThemoviedbSearchQuery() {
        URL themoviedbSearchUrl = NetworkUtils.buildUrl();
        new ThemoviedbQueryTask().execute(themoviedbSearchUrl);
    }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMoviesDataView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        mMoviesList.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the JSON
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        mMoviesList.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    public class ThemoviedbQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String themoviedbSearchResults = null;
            try {
                themoviedbSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return themoviedbSearchResults;
        }

        @Override
        protected void onPostExecute(String themoviedbSearchResults) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (themoviedbSearchResults != null && !themoviedbSearchResults.equals("")) {
                showMoviesDataView();
                MovieBox movieBox = new MovieBox(themoviedbSearchResults);
                mAdapter.setMoviesData(movieBox);

            } else {
                showErrorMessage();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        /*
         * Sets the option menu that choose which kind of movie search will be executed,
         * if popular or top rated
         */
        String queryOption = PopularMoviesPreferences.getMoviesQueryOption();

        switch (queryOption) {
            case "popular":
                menu.findItem(R.id.select_popular).setChecked(true);
                break;
            case "top_rated":
                menu.findItem(R.id.select_top_rated).setChecked(true);
                break;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemThatWasClickedId = item.getItemId();

        switch (itemThatWasClickedId) {

            case R.id.select_popular:
                PopularMoviesPreferences.setMoviesQueryOption("popular");
                mAdapter = new GreenAdapter(this);
                mMoviesList.setAdapter(mAdapter);
                loadMoviesData();
                break;

            case R.id.select_top_rated:
                PopularMoviesPreferences.setMoviesQueryOption("top_rated");
                mAdapter = new GreenAdapter(this);
                mMoviesList.setAdapter(mAdapter);
                loadMoviesData();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * This is where we receive our callback from
     * {@link com.example.android.popularmovies.GreenAdapter.ListItemClickListener}
     *
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex, JSONObject movieJSON) {

        /* if (mToast != null) {
            mToast.cancel();
        }
        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();*/

        /*
         * Storing the Context in a variable in this case is redundant since we could have
         * just used "this" or "MainActivity.this" in the method call below. However, we
         * wanted to demonstrate what parameter we were using "MainActivity.this" for as
         * clear as possible.
         */
        Context context = MainActivity.this;

        /* This is the class that we want to start (and open) when the button is clicked. */
        Class destinationActivity = ChildActivity.class;

        /*
         * Here, we create the Intent that will start the Activity we specified above in
         * the destinationActivity variable. The constructor for an Intent also requires a
         * context, which we stored in the variable named "context".
         */
        Intent startChildActivityIntent = new Intent(context, destinationActivity);

        /*
         * We use the putExtra method of the Intent class to pass some extra stuff to the
         * Activity that we are starting. Generally, this data is quite simple, such as
         * a String or a number. However, there are ways to pass more complex objects.
         */
        startChildActivityIntent.putExtra("movieJSONtoString", movieJSON.toString());

        /*
         * Once the Intent has been created, we can use Activity's method, "startActivity"
         * to start the ChildActivity.
         */
        startActivity(startChildActivityIntent);
    }

}
