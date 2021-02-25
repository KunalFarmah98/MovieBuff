package com.kunalfarmah.moviebuff.di

import android.content.Context
import androidx.room.Room
import com.kunalfarmah.moviebuff.room.MovieDao
import com.kunalfarmah.moviebuff.room.MovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideMovieDb(@ApplicationContext context: Context): MovieDatabase {
        return Room
            .databaseBuilder(
                context,
                MovieDatabase::class.java,
                MovieDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideMovieDAO(movieDatabase: MovieDatabase): MovieDao {
        return movieDatabase.blogDao()
    }
}