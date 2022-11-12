package com.kunalfarmah.moviebuff.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.provider.SyncStateContract
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.kunalfarmah.moviebuff.listener.MovieListListener
import com.kunalfarmah.moviebuff.repository.MoviesRepository
import com.kunalfarmah.moviebuff.model.Movie
import com.kunalfarmah.moviebuff.retrofit.MovieDetailsResponse
import com.kunalfarmah.moviebuff.retrofit.Movies
import com.kunalfarmah.moviebuff.retrofit.PostersItem
import com.kunalfarmah.moviebuff.retrofit.ReviewItem
import com.kunalfarmah.moviebuff.room.MovieEntity
import com.kunalfarmah.moviebuff.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MoviesViewModel
@ViewModelInject
constructor(
    private val moviesRepository: MoviesRepository,
    application: Application
) : AndroidViewModel(application) {

    private var sPref:SharedPreferences?=application.getSharedPreferences(Constants.PREF,0)

    private val _movies: MutableLiveData<List<MovieEntity>> = MutableLiveData()
    private val _movieDetails: MutableLiveData<MovieDetailsResponse> = MutableLiveData()
    private val _movieReviews: MutableLiveData<List<ReviewItem>> = MutableLiveData()
    private val _movieImages: MutableLiveData<List<PostersItem>> = MutableLiveData()


    val movies: MutableLiveData<List<MovieEntity>>
        get() = _movies

    val movieDetails: MutableLiveData<MovieDetailsResponse>
        get() = _movieDetails

    val movieReviews: MutableLiveData<List<ReviewItem>>
        get() = _movieReviews

    val movieImages: MutableLiveData<List<PostersItem>>
        get() = _movieImages




    fun fetchAllMovies(listener: MovieListListener){
        moviesRepository.fetchMovies(listener)
    }

    fun searchAllMovies(listener: MovieListListener,  query:String){
        moviesRepository.searchMovies(listener, query)
    }


    fun getAllMovies(){
        viewModelScope.launch {
            movies.value = moviesRepository.getMovies()
        }
    }

    fun getMovieDetail(id:String){
        var details:MovieDetailsResponse?=null
        viewModelScope.launch {
            details = moviesRepository.getMovieDetails(id)
        }.invokeOnCompletion { movieDetails.value = details }
    }

    fun getMovieReviews(id:String){
        var reviews:List<ReviewItem?>?=null
        viewModelScope.launch {
            var response = moviesRepository.getMovieReviews(id)
            if(null!=response && null!=response.results)
                reviews = response.results
        }.invokeOnCompletion { movieReviews.value = reviews as List<ReviewItem>? }
    }

    fun getMovieImages(id:String){
        var posters:List<PostersItem?>?=null
        viewModelScope.launch {
            var response = moviesRepository.getMovieImages(id)
            if(response?.posters != null)
                posters = response.posters
        }.invokeOnCompletion { movieImages.value = posters as List<PostersItem>? }
    }

    fun setSelectedMovie(id:Int){
        sPref?.edit()?.putInt(Constants.MOVIE_ID,id)?.apply()
    }

    fun getSelectedMovie(): String{
        return sPref?.getInt(Constants.MOVIE_ID,0).toString()
    }


}
