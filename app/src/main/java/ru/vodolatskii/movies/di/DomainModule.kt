package ru.vodolatskii.movies.di

import dagger.Binds
import dagger.Module
import ru.vodolatskii.movies.data.repositiryImpl.MovieRepositoryImpl
import ru.vodolatskii.movies.domain.MovieRepository
import javax.inject.Singleton


@Module
abstract class DomainModule {

    @Singleton
    @Binds
    abstract fun provideRepository(repository: MovieRepositoryImpl) : MovieRepository
}