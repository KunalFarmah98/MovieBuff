package com.kunalfarmah.moviebuff.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import com.kunalfarmah.moviebuff.listener.MovieListListener
import com.kunalfarmah.moviebuff.repository.MoviesRepository
import com.kunalfarmah.moviebuff.retrofit.MovieDetailsResponse
import com.kunalfarmah.moviebuff.retrofit.PostersItem
import com.kunalfarmah.moviebuff.retrofit.ReviewItem
import com.kunalfarmah.moviebuff.model.Movie
import com.kunalfarmah.moviebuff.preferences.PreferenceManager
import com.kunalfarmah.moviebuff.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel
@Inject constructor(
    private val moviesRepository: MoviesRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _movies: MutableLiveData<List<Movie>> = MutableLiveData()
    private val _movieDetails: MutableLiveData<MovieDetailsResponse> = MutableLiveData()
    private val _movieReviews: MutableLiveData<List<ReviewItem>> = MutableLiveData()
    private val _movieImages: MutableLiveData<List<PostersItem>> = MutableLiveData()


    val movies: MutableLiveData<List<Movie>>
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
        movies.value = moviesRepository.getMovies()
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
            if(response?.results != null)
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
        PreferenceManager.putValue(Constants.MOVIE_ID, id)
    }

    fun getSelectedMovie(): String{
        return PreferenceManager.getValue(Constants.MOVIE_ID, 0).toString()
    }


}
