package com.kunalfarmah.moviebuff.util

import com.kunalfarmah.moviebuff.BuildConfig

class Constants {

    companion object{
        val MOVIE_ID = "MOVIE_ID"
        val PREF = "MOVIE_BUFF"
        val API_KEY = BuildConfig.API_KEY
        val BASE_URL = "https://api.themoviedb.org/3/"
        val PREFS_NAME = "MovieBuffPrefs"
        val SELECTED_FILTER = "SelectedFilter"
        val SORT_ORDER = "SortOrder"
    }

     object SortOrder {
         val POPULAIRTY = "Popularity"
         val RELEASE_DATE = "ReleaseData"
         val RATING = "Rating"
    }

}