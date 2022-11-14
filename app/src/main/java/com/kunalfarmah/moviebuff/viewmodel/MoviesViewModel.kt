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
import kotlinx.coroutines.flow.Flow
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




    fun fetchAllMovies() {
        viewModelScope.launch {
            movies.value = moviesRepository.fetchMovies()
        }
    }

    fun searchAllMovies(query:String){
        viewModelScope.launch {
            movies.value = moviesRepository.searchMovies(query)
        }
    }


    fun getAllMovies(){
        movies.value = moviesRepository.getMovies()
    }

    fun getMovieDetail(id:String){
        viewModelScope.launch {
            val response = moviesRepository.getMovieDetails(id)
            if(response != null){
                movieDetails.value = response
            }
        }
    }

    fun getMovieReviews(id:String){
        viewModelScope.launch {
            val response = moviesRepository.getMovieReviews(id)
            if(response?.results != null)
                movieReviews.value  = response.results as? List<ReviewItem>
        }
    }

    fun getMovieImages(id:String){
        viewModelScope.launch {
            val response = moviesRepository.getMovieImages(id)
            if(response?.posters != null)
                movieImages.value = response.posters as List<PostersItem>?
        }
    }

    fun setSelectedMovie(id:Int){
        PreferenceManager.putValue(Constants.MOVIE_ID, id)
    }

    fun getSelectedMovie(): String{
        return PreferenceManager.getValue(Constants.MOVIE_ID, 0).toString()
    }

    fun getPrefsValue(flow: Flow<Any?>?, type: Int): Any? {
        var value: Any? = null
        viewModelScope.launch {
            flow?.collect{
                value = it
            }
        }
        if(value == null)
            return null
        when (type){
            0 -> return value as Int
            1 -> return value as Float
            2 -> return value as Long
            3 -> return value as String
            4 -> return value as Boolean
            5 -> return value as Set<String>
        }
        return value

    }


}
