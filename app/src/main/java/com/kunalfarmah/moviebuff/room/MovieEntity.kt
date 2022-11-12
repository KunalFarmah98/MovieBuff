package com.kunalfarmah.moviebuff.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
class MovieEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "image")
    var image: String,

    @ColumnInfo(name = "popularity")
    var popularity: Double,

    @ColumnInfo(name = "genre_ids")
    val genreIds: String,

    @ColumnInfo(name = "release_date")
    val releaseDate: String,

    @ColumnInfo(name = "vote_average")
    val voteAverage: Double
)



