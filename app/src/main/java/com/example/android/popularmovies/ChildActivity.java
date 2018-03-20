package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.popularmovies.data.PopularMoviesPreferences;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This activity will render the movie details
 */
public class ChildActivity extends AppCompatActivity {

    private TextView mDisplayTitle;
    private ImageView mDisplayPoster;
    private TextView mDisplayOverview;
    private TextView mDisplayVoteAverage;
    private TextView mDisplayReleaseDate;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        mDisplayTitle = (TextView) findViewById(R.id.tv_title);
        mDisplayPoster = (ImageView) findViewById(R.id.iv_poster);
        mDisplayOverview = (TextView) findViewById(R.id.tv_overview);
        mDisplayVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
        mDisplayReleaseDate = (TextView) findViewById(R.id.tv_release_date);

        Context context = ChildActivity.this;

        /*
         * Here is where all the magic happens. The getIntent method will give us the Intent that
         * started this particular Activity.
         */
        Intent intentThatStartedThisActivity = getIntent();

         /*
         * Although there is always an Intent that starts any particular Activity, we can't
         * guarantee that the extra we are looking for was passed as well. Because of that, we need
         * to check to see if the Intent has the extra that we specified when we created the
         * Intent that we use to start this Activity. Note that this extra may not be present in
         * the Intent if this Activity was started by any other method.
         * */
        if (intentThatStartedThisActivity.hasExtra("movieJSONtoString")) {

            /*
             * Now that we've checked to make sure the extra we are looking for is contained within
             * the Intent, we can extract the extra. To do that, we simply call the getStringExtra
             * method on the Intent. There are various other get*Extra methods you can call for
             * different types of data. Please feel free to explore those yourself.
             */

            String movieJSONtoString = intentThatStartedThisActivity.getStringExtra("movieJSONtoString");

            try {
                JSONObject movieJSON = new JSONObject(movieJSONtoString);

                 /*
                 * Finally, we can set the text of our TextView (using setText) to the text that was
                 * passed to this Activity.
                 */
                String movieTitle = movieJSON.getString("title");
                mDisplayTitle.setText(movieTitle);

                String movieOverview = movieJSON.getString("overview");
                mDisplayOverview.setText(movieOverview);

                String movieVoteAverage = movieJSON.getString("vote_average");
                mDisplayVoteAverage.setText(movieVoteAverage);

                String movieReleaseDate = movieJSON.getString("release_date");
                mDisplayReleaseDate.setText(movieReleaseDate);

                try {

                    String posterPath = movieJSON.getString("poster_path");
                    String urlString = PopularMoviesPreferences.getThemoviedbPosterUrl(posterPath);

                    Picasso.with(context)
                            .load(urlString)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(mDisplayPoster);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
