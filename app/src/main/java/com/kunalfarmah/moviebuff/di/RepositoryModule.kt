package com.kunalfarmah.moviebuff.di

import com.kunalfarmah.moviebuff.repository.MoviesRepository
import com.kunalfarmah.moviebuff.retrofit.MovieRetrofit
import com.kunalfarmah.moviebuff.retrofit.NetworkMapper
import com.kunalfarmah.moviebuff.room.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        movieDao: MovieDao,
        retrofit: MovieRetrofit,
        networkMapper: NetworkMapper
    ): MoviesRepository {
        return MoviesRepository(movieDao, retrofit, networkMapper)
    }
}














