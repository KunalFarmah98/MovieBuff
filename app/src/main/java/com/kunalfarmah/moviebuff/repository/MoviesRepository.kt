package com.kunalfarmah.moviebuff.repository

import com.kunalfarmah.moviebuff.listener.MovieListListener
import com.kunalfarmah.moviebuff.util.Constants
import com.kunalfarmah.moviebuff.retrofit.*
import com.kunalfarmah.moviebuff.room.MovieDao
import com.kunalfarmah.moviebuff.room.MovieEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesRepository
constructor(
    private val movieDao: MovieDao,
    private val movieRetrofit: MovieRetrofit,
    private val networkMapper: NetworkMapper
) {
    fun fetchMovies(listener: MovieListListener) {
        var movieList: ArrayList<MovieEntity>?

        movieRetrofit.getMovies(Constants.API_KEY, "US").enqueue(object : Callback<MoviesResponse> {
            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                if (response.isSuccessful && null != response.body()) {
                    movieList = ArrayList()
                    var movies = response.body()?.results ?: ArrayList()
                    for (movie in movies)
                        (movieList as ArrayList<MovieEntity>).add(networkMapper.mapToEntity(movie))

                    listener.setView(movieList!!)
                    CoroutineScope(Dispatchers.IO).launch {
                        for (movie in movies) {
                            movieDao.insert(networkMapper.mapToEntity(movie))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                listener.setNoInternetView()
            }

        })
    }

    fun searchMovies(listener: MovieListListener, query: String) {
        var movieList: ArrayList<MovieEntity>?

        movieRetrofit.searchMovies(Constants.API_KEY, query).enqueue(object : Callback<MoviesResponse> {
            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                if (response.isSuccessful && null != response.body()) {
                    movieList = ArrayList()
                    var movies = response.body()?.results
                    for (movie in movies!!)
                        (movieList as ArrayList<MovieEntity>).add(networkMapper.mapToEntity(movie))
                    listener.setView(movieList!!)
                }
            }

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                listener.setNoInternetView()
            }

        })
    }

    suspend fun getMovies(): List<MovieEntity> {
        return movieDao.get()
    }

    suspend fun getMovieDetails(id: String): MovieDetailsResponse? {
        return movieRetrofit.getDetails(id, Constants.API_KEY)
    }

    suspend fun getMovieImages(id: String): ImageResponse? {
        return movieRetrofit.getImages(id, Constants.API_KEY)
    }

    suspend fun getMovieReviews(id: String): ReviewResponse? {
        return movieRetrofit.getReviews(id, Constants.API_KEY)
    }
}