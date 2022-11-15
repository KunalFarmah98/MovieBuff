package com.kunalfarmah.moviebuff.util

class Constants {

    companion object{
        val MOVIE_ID = "MOVIE_ID"
        val PREF = "MOVIE_BUFF"
        val API_KEY = "8c7500bde33357f5fa1314eb3ef4ca5d"
        val BASE_URL = "https://api.themoviedb.org/3/"
        val PREFS_NAME = "MovieBuffPrefs"
        val SELECTED_FILTER = "SelectedFilter"
        val SORT_ORDER = "SortOrder"
        val MOVIES = "Movies"
        val DISPLAY = "Display"
    }

     object SortOrder {
         val POPULAIRTY = "Popularity"
         val RELEASE_DATE = "ReleaseDate"
         val RATING = "Rating"
    }

    object Display {
        val GRID = "Grid"
        val CARDS = "Cards"
    }

}