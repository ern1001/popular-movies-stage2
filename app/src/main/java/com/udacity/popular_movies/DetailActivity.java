package com.udacity.popular_movies;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.udacity.popular_movies.utils.MovieDBInfo;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getCanonicalName();
    private static final int PICTURE_WIDTH = 500;
    //Butterknife injections:
    @BindView(R.id.detailImage) ImageView detailImage;
    @BindView(R.id.detailTitle) TextView detailTitle;
    @BindView(R.id.detailOverview) TextView detailOverview;
    @BindView(R.id.detailDate) TextView detailDate;
    @BindView(R.id.detailRating) TextView detailRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this); //Butterknife binding
        setTitle(R.string.details);

        //get bundle info
        Bundle bundle = getIntent().getExtras();
        int position = 1;
        if (bundle != null) {
            position = bundle.getInt("position");
        }
        Log.i("TAG", "Position: " + position );

        //setup views
        //ImageView detailImage = findViewById(R.id.detailImage);
        //TextView detailTitle = findViewById(R.id.detailTitle);
        //TextView detailOverview = findViewById(R.id.detailOverview);
        //TextView detailDate = findViewById(R.id.detailDate);
        //TextView detailRating = findViewById(R.id.detailRating);

        String posterURL="R.drawable.errormsg"; //will show error msg in Picasso below if it cant be set
        //Get the movies list
        List moviesList = MainActivity.getMoviesList();
        //process the movies list based upon the position number
        if (moviesList != null && moviesList.size()>0 ) {
            MovieDBInfo mdbInfo = (MovieDBInfo) moviesList.get(position);
            //Movie Title text
            String mTitle = mdbInfo.getTitle();
            Log.i(TAG, "Title: " + mdbInfo.getTitle());
            detailTitle.setText(mTitle);
            //Build the url for the poster picture
            String pictureWidth = Integer.toString(PICTURE_WIDTH) + "/";
            StringBuilder sb = new StringBuilder(50);
            String posterBaseUrl = "http://image.tmdb.org/t/p/";
            sb.append(posterBaseUrl);
            sb.append("w");
            sb.append(pictureWidth);
            sb.append(mdbInfo.getPosterPath());
            //posterURL = posterBaseUrl + "w" + pictureWidth + mdbInfo.getPosterPath();
            posterURL = sb.toString();

            //Overview text
            String overview = getResources().getText(R.string.overview) + "\n"+ mdbInfo.getOverview();
            detailOverview.setText(overview);
            //Date text
            String date = getResources().getText(R.string.date) + mdbInfo.getReleaseDate();
            detailDate.setText(date);
            //Rating text
            String rating = getResources().getText(R.string.voteAvg) + mdbInfo.getVoteAverage().toString();
            detailRating.setText(rating);
        }

        Log.i(TAG, "PosterURL: "+ posterURL);

        Picasso.with(this).load(posterURL)
                //.fit()
                .resize(PICTURE_WIDTH, 0)
                .placeholder(R.drawable.loadingmsg)
                .error(R.drawable.errormsg)
                .into(detailImage);
    }
}
