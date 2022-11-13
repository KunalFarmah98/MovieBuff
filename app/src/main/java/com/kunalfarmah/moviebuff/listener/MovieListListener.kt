package com.kunalfarmah.moviebuff.listener

import com.kunalfarmah.moviebuff.model.Movie

interface MovieListListener {
     fun setView(list:ArrayList<Movie>)
     fun setNoInternetView()
}