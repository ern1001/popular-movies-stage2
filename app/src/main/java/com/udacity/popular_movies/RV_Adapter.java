package com.udacity.popular_movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.udacity.popular_movies.utils.MovieDBInfo;

public class RV_Adapter extends RecyclerView.Adapter<RV_Adapter.ViewHolder>{
    private static final String TAG = RV_Adapter.class.getCanonicalName();
    private final LayoutInflater rvInflater;
    private ItemClickListener rvClickListener;
    private final Context mContext;
    private List moviesList;

    // Constructor
    RV_Adapter(Context context, List moviesList) {
        this.rvInflater = LayoutInflater.from(context);
        this.mContext=context;
        this.moviesList = moviesList;
    }

    //Inflate the view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = rvInflater.inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(view) {
        };
    }

    //Bind data to each rv item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String posterURL="R.drawable.errormsg"; //will show error msg in Picasso below if it cant be set
        moviesList = MainActivity.getMoviesList();
        if (moviesList != null && moviesList.size()>0 ) {
            MovieDBInfo mdbInfo = (MovieDBInfo) moviesList.get(position);
            String mTitle = mdbInfo.getTitle();
            Log.i(TAG, "Title: " + mdbInfo.getTitle());
            holder.rvTextView.setText(mTitle);
            //get the poster picture width
            String pictureWidth = Integer.toString(MainActivity.PICTURE_WIDTH);
            //Build the poster url
            StringBuilder sb = new StringBuilder(50);
            String posterBaseUrl = "http://image.tmdb.org/t/p/";
            sb.append(posterBaseUrl);
            sb.append("w");
            sb.append(pictureWidth);
            sb.append("/");
            sb.append(mdbInfo.getPosterPath());
            //String pictureWidth = Integer.toString(MainActivity.PICTURE_WIDTH) + "/";
            //posterURL = posterBaseUrl + "w" + pictureWidth + mdbInfo.getPosterPath();
            posterURL = sb.toString();
        }

        Log.i(TAG, "PosterURL: "+ posterURL);

        //Get the poster picture and put into the rv item image view
        Picasso.with(mContext).load(posterURL)
                    //.fit()
                    .resize(MainActivity.PICTURE_WIDTH, 0)
                    .placeholder(R.drawable.loadingmsg)
                    .error(R.drawable.errormsg)
                    .into(holder.rvImageView);
    }

    //Returns rv count
    @Override
    public int getItemCount() {
        int items =0;
        if (moviesList != null) {
            items = moviesList.size();
        }
        Log.i(TAG, "RV Items: " + items);
        return items;
    }

    //View holder for the recycler view
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView rvImageView;
        final TextView rvTextView;

        //Constructor
        ViewHolder(View itemView){
            super(itemView);
            rvImageView = (ImageView) itemView.findViewById(R.id.image_view);
            rvTextView =  (TextView) itemView.findViewById(R.id.info_text);
            itemView.setOnClickListener(this);
        }

        //Required -
        @Override
        public void onClick(View v) {
            if (rvClickListener != null) rvClickListener.on_rvItemClick(v, getAdapterPosition());
        }

    }

    //Setter - sets onclicklistener to rv item , invoked in MainActivity
    void set_rvClickListener(ItemClickListener itemClickListener) {
        this.rvClickListener = itemClickListener;
    }

    //Used byMainActivity to listen for recycler view item click
    public interface ItemClickListener {
        void on_rvItemClick(View view, int position);
    }

}
