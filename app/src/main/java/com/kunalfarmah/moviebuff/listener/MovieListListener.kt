package com.kunalfarmah.moviebuff.listener

import com.kunalfarmah.moviebuff.room.MovieEntity

interface MovieListListener {
     fun setView(list:ArrayList<MovieEntity>)
     fun setNoInternetView()
}