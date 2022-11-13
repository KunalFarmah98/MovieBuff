package com.kunalfarmah.moviebuff.di

import com.kunalfarmah.moviebuff.repository.MoviesRepository
import com.kunalfarmah.moviebuff.retrofit.MovieRetrofit

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        retrofit: MovieRetrofit,
    ): MoviesRepository {
        return MoviesRepository(retrofit)
    }
}














