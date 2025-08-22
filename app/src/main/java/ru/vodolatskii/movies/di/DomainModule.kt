package ru.vodolatskii.movies.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.vodolatskii.movies.data.repositiryImpl.MovieRepositoryImpl
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.presentation.viewmodels.ViewModelFactory
import javax.inject.Singleton


@Module
abstract class DomainModule {

    @Singleton
    @Binds
    abstract fun provideRepository(repository: MovieRepositoryImpl) : MovieRepository

    @Binds
    @Singleton
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}