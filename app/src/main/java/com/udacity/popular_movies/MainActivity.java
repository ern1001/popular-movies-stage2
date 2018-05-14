package com.udacity.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.util.List;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.udacity.popular_movies.utils.apiPopular;
import com.udacity.popular_movies.utils.apiTopRated;
import com.udacity.popular_movies.utils.MovieDBModel;


public class MainActivity extends AppCompatActivity implements RV_Adapter.ItemClickListener {
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final String MY_PREFS_NAME = "MyPrefsFile"; //for storing/retrieving settings
    private SharedPreferences sharedPrefs; //for storing/retrieving settings
    private boolean isForeground;// true if mainactivity is in foreground
    private Boolean prefPopular = true; // pref setting - true: sort movies by most Popular , false: sort movies by rating
    private Boolean gotPopular ; //true if displayed movies were fetched using most Popular query
    private int number_of_columns ; //columns in gridview
    private static List movies_list; //list of movies received from themoviedb.org (tmdb)
    //Butterknife imhections
    @BindView(R.id.defaultTextView) TextView defaultTextView;//using butterknife code injection
    @BindString(R.string.wait) String waitMsg;
    //private TextView defaultTextView; //for startup msg and error msg display
    //Get the API Key from gradle.properties so that it is secured and not part of source code on github
    public static final String API_KEY = BuildConfig.MY_MOVIE_DB_API_KEY; //for accessing thmdb website
    private static final int PICTURE_PAD = 50; //Addl room needed when calculating the # of columns needed
    public static final int PICTURE_WIDTH = 185 ; //Picture width in the grid display


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); //Butterknife binding
        //defaultTextView=findViewById(R.id.defaultTextView); //visible while waiting for retrofit result
        //defaultTextView.setText(R.string.wait); //please wait msg
        defaultTextView.setText(waitMsg); //please wait msg

        //Note - the recycler view is initialized/updated by the callback setup in getRetrofitObject/retrofitCall
        // getRetrofitOject is invoked in onStart so the recyclerView is updated on lifecycle events, such as returning from the settings activity
    }//End onCreate

    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG, "onStart");
        isForeground=true;

        //Get saved preferences settings
        sharedPrefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean ap = getAppPreferences(); //retrieve saved preferences
        if (!ap) { //see if we need to initialize prefs
            initPrefs(); //initialize preferences
        }

        //Set number of columns to be displayed in grid based upon screen width
        number_of_columns = getNumberofColumns();

        //gotPopular will be null when first starting the app & orientation changes
        //if gotPopular is not initialized, set it opposite of prefPopular - it will be updated in getRetrofitObject
        if (gotPopular == null) {gotPopular = !prefPopular;}

        //Check to see if the sort criteria has changed
        //If so, use getRetrofitObject and it's callback to obtain new movies_list and update the recycler view
        //Otherwise just initialize recycler view and hide the text view
        if ( prefPopular != gotPopular || movies_list == null ) {
            //check to see if connected to internet
            if (checkInternet()) {
                movies_list = getRetrofitObject();//fetch tmdb data online & put results into movie list
            } else {
                defaultTextView.setText(R.string.no_internet);
                //Toast.makeText(this, R.string.no_internet , Toast.LENGTH_SHORT).show();
            }
        }else {
            //don't need to fetch new movie_list
            //Setup recycler view to show current list
            defaultTextView.setVisibility(View.INVISIBLE); //hide this default text now
            initRecyclerView(); //
        }
    }//End onStart


    // Sets up the recyclerview
    private void initRecyclerView() {
        //Recycler View setup
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        //Use GridLayoutManager to make the grid of movie posters
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, number_of_columns));
        //setup recycler view adapter
        RV_Adapter rvAdapter = new RV_Adapter(this, movies_list);
        rvAdapter.set_rvClickListener(this); //set to listen for clicks from a recycler view item
        mRecyclerView.setAdapter(rvAdapter);
    }

    //Sets up the retrofit call to get tmdb data using API model , returns the movies list
    //Calls retrofitCall which retrieves the movies_list and creates a callback to initialize/update the recyclerView
    private List getRetrofitObject() {
        String tmdbBaseUrl = "http://api.themoviedb.org/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(tmdbBaseUrl)
                .addConverterFactory((GsonConverterFactory.create()))
                .build();

        if (prefPopular) {
            setTitle(R.string.popular); //Set activity title header appropriately
            apiPopular service = retrofit.create(apiPopular.class);
            Call<MovieDBModel> call = service.getJsonObjectData();
            movies_list = retrofitCall(call);//get the movies list using the method below
            gotPopular = true; // set it accordingly  - used to determine if the retrieved movie list sort criteria has changed
        }else {
            setTitle(R.string.rated); //Set activity title header appropriately
            apiTopRated service = retrofit.create(apiTopRated.class);
            Call<MovieDBModel> call = service.getJsonObjectData();
            movies_list = retrofitCall(call); //get the movies list using the method below
            gotPopular = false; //set it accordingly - used to determine if the retrieved movie list sort criteria has changed
        }
        return movies_list;
    }//end getRetrofitObject

    //Retrofit call and callback, input is a call, returns the movie list
    private List retrofitCall(Call call) {
        call.enqueue(new Callback<MovieDBModel>() {
            @Override
            public void onResponse(Call<MovieDBModel> call, Response<MovieDBModel> response) {
                if (response.body() != null) {
                    Log.i(TAG, "Response " + response.body().toString());
                    Log.i(TAG, "Results " + response.body().getResults().toString());
                    movies_list = response.body().getResults();
                    //Now since we have the data, setup the recycler view
                    defaultTextView.setVisibility(View.INVISIBLE); //hide this default text now
                    initRecyclerView();
                }
                else {
                    Log.e(TAG, "null response.body");
                }
            }

            @Override
            public void onFailure(Call<MovieDBModel> call, Throwable t) {
                Log.e(TAG, "  error " + t.toString());
            }
        });
        return movies_list;
    }//end retrofitCall


// Getter for movies_list list
    public static List getMoviesList() {
        return movies_list;
    }

    //returns number of grid columns based on screen width
    private int getNumberofColumns() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        //get the display width
        display.getSize(size);
        int width = size.x;
        int columns = width / (PICTURE_WIDTH + PICTURE_PAD); //set number of columns based on screen width
        Log.i("TAG", "Number of Columns: " + number_of_columns);
        return columns;
    }

    // Listen for recycler view clicks
    @Override
    public void on_rvItemClick(View view, int position) {
        Log.i("TAG", "Clicked on RV item " + position );
        //Toast.makeText(this, "You clicked on position: " + position, Toast.LENGTH_SHORT).show();
        //Launch detail activity
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        detailIntent.putExtras(bundle);
        startActivity(detailIntent);
    }

    //----------------------------- Shared Preferences ---------------------------------------
    //Check to see if network connectivity
    private boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         return cm.getActiveNetworkInfo() != null;
    }

    //Get app saved preferences
    private boolean getAppPreferences() {
        String validText = sharedPrefs.getString("text", null);
        if (validText != null) {
            prefPopular = sharedPrefs.getBoolean("prefPopular", true);
            return true; //preferences are initialized
        } else {
            return false; // preferences not initialized yet
        }
    }

    private void initPrefs() {
        SharedPreferences.Editor editor;
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("text", "OK"); //so it's not null
        editor.putBoolean("prefPopular", true); //
        editor.apply();//save it into preferences
    }

    //----------------------------- Android Lifecycle ---------------------------------------
    @Override
    protected void onStop(){
        super.onStop();
        Log.i(TAG, "onStop");
        isForeground=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        isForeground = true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "onPause");
        isForeground=false;
    }


    //---------------- Options Menu Navigation -----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {  //Settings screen
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



}
