package com.kunalfarmah.moviebuff.retrofit


import retrofit2.Call
import retrofit2.http.*

interface MovieRetrofit {

    @GET("movie/popular")
    suspend fun getMovies(@Query("api_key") apiKey:String, @Query("region") region:String): MoviesResponse

    @GET("search/movie")
    suspend fun searchMovies(@Query("api_key") apiKey:String, @Query("query") query:String): MoviesResponse

    @GET("movie/{movieId}")
    suspend fun getDetails(@Path("movieId") id:String, @Query("api_key") apiKey:String): MovieDetailsResponse

    @GET("movie/{movieId}/reviews")
    suspend fun getReviews(@Path("movieId") id:String, @Query("api_key") apiKey:String): ReviewResponse

    @GET("movie/{movieId}/images")
    suspend fun getImages(@Path("movieId") id:String, @Query("api_key") apiKey:String): ImageResponse

}