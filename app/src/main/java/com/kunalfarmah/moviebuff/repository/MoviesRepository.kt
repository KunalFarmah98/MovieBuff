package com.kunalfarmah.moviebuff.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kunalfarmah.moviebuff.model.Movie
import com.kunalfarmah.moviebuff.preferences.PreferenceManager
import com.kunalfarmah.moviebuff.util.Constants
import com.kunalfarmah.moviebuff.retrofit.*
import com.kunalfarmah.moviebuff.util.Util

class MoviesRepository
constructor(
    private val movieRetrofit: MovieRetrofit,
) {
    suspend fun fetchMovies() : ArrayList<Movie> {
        var movieList: ArrayList<Movie>?

        return if(Util.isNetworkAvailable()) {
            val response = movieRetrofit.getMovies(Constants.API_KEY, "US")
            movieList = ArrayList()
            val movies = response.results ?: ArrayList()
            for (movie in movies)
                movieList.add(mapToObject(movie))
            PreferenceManager.putValue(Constants.MOVIES, Gson().toJson(movieList))
            movieList
        } else{
            val type =  object : TypeToken<List<Movie>>() {}.type
            val movies = PreferenceManager.getValue(Constants.MOVIES, "") as String
            if(movies.isNotEmpty())
                Gson().fromJson(movies, type) as ArrayList<Movie>
            else
                ArrayList()
        }


        /*movieRetrofit.getMovies(Constants.API_KEY, "US").enqueue(object : Callback<MoviesResponse> {
            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                if (response.isSuccessful && null != response.body()) {
                    movieList = ArrayList()
                    var movies = response.body()?.results ?: ArrayList()
                    for (movie in movies)
                        (movieList as ArrayList<Movie>).add(mapToObject(movie))
                    listener.setView(movieList!!)
                    PreferenceManager.putValue(Constants.MOVIES, Gson().toJson(movieList))
                    return movieList
                }
            }

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                listener.setNoInternetView()
            }

        })*/
    }

/*    fun searchMovies(listener: MovieListListener, query: String){

        movieRetrofit.searchMovies(Constants.API_KEY, query).enqueue(object : Callback<MoviesResponse> {
            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                if (response.isSuccessful && null != response.body()) {
                    movieList = ArrayList()
                    var movies = response.body()?.results
                    for (movie in movies!!)
                        (movieList as ArrayList<Movie>).add(mapToObject(movie))
                    listener.setView(movieList!!)
                }
            }

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                listener.setNoInternetView()
            }

        })
    }*/

    suspend fun searchMovies(query: String): ArrayList<Movie> {
        var movieList: ArrayList<Movie>?

        return if (Util.isNetworkAvailable()) {
            val response = movieRetrofit.searchMovies(Constants.API_KEY, query)
            movieList = ArrayList()
            val movies = response.results
            for (movie in movies!!)
                movieList.add(mapToObject(movie))
            movieList
        } else
            ArrayList()
    }

    fun getMovies(): List<Movie> {
        val type =  object : TypeToken<List<Movie>>() {}.type
        val movies = PreferenceManager.getValue(Constants.MOVIES, "") as String
        return Gson().fromJson(movies, type) as List<Movie>
    }

    suspend fun getMovieDetails(id: String): MovieDetailsResponse? {
        return if(Util.isNetworkAvailable()) {
            val response = movieRetrofit.getDetails(id, Constants.API_KEY)
            PreferenceManager.putValue(id + "_details", Gson().toJson(response))
            response
        } else{
            val type =  object : TypeToken<MovieDetailsResponse>() {}.type
            val cache = PreferenceManager.getValue(id + "_details", "") as String
            if(cache.isNotEmpty())
                 Gson().fromJson(cache, type) as MovieDetailsResponse
            else
                null
        }
    }

    suspend fun getMovieImages(id: String): ImageResponse? {
        return if(Util.isNetworkAvailable()) {
            val response = movieRetrofit.getImages(id, Constants.API_KEY)
            PreferenceManager.putValue(id + "_images", Gson().toJson(response))
            response
        }
        else{
            val type =  object : TypeToken<ImageResponse>() {}.type
            val cache = PreferenceManager.getValue(id + "_images", "") as String
            if(cache.isNotEmpty())
                 Gson().fromJson(cache, type) as ImageResponse
            else
                null
        }
    }

    suspend fun getMovieReviews(id: String): ReviewResponse? {
        return if(Util.isNetworkAvailable()) {
            val response = movieRetrofit.getReviews(id, Constants.API_KEY)
            PreferenceManager.putValue(id + "_reviews", Gson().toJson(response))
            return response
        }
        else{
            val type =  object : TypeToken<ReviewResponse>() {}.type
            val cache = PreferenceManager.getValue(id + "_reviews", "") as String
           if(cache.isNotEmpty())
                Gson().fromJson(cache, type) as ReviewResponse
            else
                null
        }
    }

    private fun mapToObject(response: Movies): Movie {
        return Movie(
            response.id as Int,
            response.title ?: "",
            response.posterPath ?: "",
            response.popularity ?: 0.0,
            Gson().toJson(response.genreIds ?: ArrayList<Int>()),
            response.releaseDate ?: "",
            response.voteAverage ?: 0.0
        )
    }
}