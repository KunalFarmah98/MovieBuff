package com.kunalfarmah.moviebuff.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movieEntity: MovieEntity): Long

    @Query("SELECT * FROM movies")
    suspend fun get(): List<MovieEntity>
}