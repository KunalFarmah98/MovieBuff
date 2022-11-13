package com.kunalfarmah.moviebuff.model

data class Movie(
    var id: Int,
    var title: String,
    var image: String,
    var popularity: Double,
    val genreIds: String,
    val releaseDate: String,
    val voteAverage: Double
){}




