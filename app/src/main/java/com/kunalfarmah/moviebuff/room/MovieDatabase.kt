package com.kunalfarmah.moviebuff.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class ], version = 2, exportSchema = false)
abstract class MovieDatabase: RoomDatabase() {

    abstract fun blogDao(): MovieDao

    companion object{
        val DATABASE_NAME: String = "movie_db"
    }


}