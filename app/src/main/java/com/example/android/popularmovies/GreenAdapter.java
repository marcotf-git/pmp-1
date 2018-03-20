package com.example.android.popularmovies;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.example.android.popularmovies.data.PopularMoviesPreferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Using Recycler VIew and ViewHolders
 */

public class GreenAdapter extends RecyclerView.Adapter<GreenAdapter.MovieViewHolder> {

    private static final String TAG = GreenAdapter.class.getSimpleName();

    private static int viewHolderCount;

    private MovieBox movieBox;


    /**
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, JSONObject movieJSON);
    }

    /**
     * Constructor for GreenAdapter that accepts a number of items to display and the specification
     * for the ListItemClickListener.
     *
     * @param listener Listener for list item clicks
     */
    public GreenAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
        viewHolderCount = 0;
    }

    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        Log.d(TAG, "#" + position);

        JSONObject movieJSON = movieBox.getMovieJSON(position);

        String posterPath;
        final String posterTitle;

        try {
            posterPath = movieJSON.getString("poster_path");
            posterTitle = movieJSON.getString("title");

            //String urlString = "https://image.tmdb.org/t/p/w185/" + posterPath;

            String urlString = PopularMoviesPreferences.getThemoviedbPosterUrl(posterPath);

            Log.v(TAG, "urlString:"+urlString);

            /*
             * Use the call back of picasso to manage the error in loading poster.
             * On error, write the movie title in the text view that is together with the
             * image view, and make it visible.
             */
            Picasso.with(holder.context)
                    .load(urlString)
                    .placeholder(R.drawable.ic_poster)
                    .into(holder.posterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.v(TAG, "Poster loaded");
                            holder.posterTextView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            Log.e(TAG, "Error in loading poster");
                            holder.posterTextView.setText(posterTitle);
                            holder.posterTextView.setVisibility(View.VISIBLE);
                    }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {

        if (null == movieBox) return 0;
        return movieBox.getNumberOfMovies();
    }

    /**
     * This method is used to set the movies list on a GreenAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new GreenAdapter to display it.
     *
     * @param movieBox The object with the result of the query to the themoviedb.org to be displayed.
     */
    public void setMoviesData(MovieBox movieBox) {
        this.movieBox = movieBox;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a list item.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {

        ImageView posterImageView;
        TextView posterTextView;
        Context context;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link GreenAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        private MovieViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            posterImageView = (ImageView) itemView.findViewById(R.id.iv_main_poster);
            posterTextView = (TextView) itemView.findViewById(R.id.tv_main_poster);

            // Call setOnClickListener on the View passed into the constructor (use 'this' as the OnClickListener)
            itemView.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition, movieBox.getMovieJSON(clickedPosition));
        }
    }

}
