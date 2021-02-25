package com.kunalfarmah.moviebuff.listener

import android.widget.ImageView

interface MovieClickListener{
    fun onMovieClick(id:Int, image:ImageView)
}