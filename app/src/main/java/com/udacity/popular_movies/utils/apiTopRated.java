package com.udacity.popular_movies.utils;
import com.udacity.popular_movies.MainActivity;

import retrofit2.Call;
import retrofit2.http.GET;

public interface apiTopRated {

    //Popular movie API request
    @GET("3/movie/top_rated?api_key=" + MainActivity.API_KEY )
        Call<MovieDBModel>getJsonObjectData();
}
