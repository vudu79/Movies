package ru.vodolatskii.movies.di

import android.content.Context
import androidx.annotation.StringRes
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.vodolatskii.movies.data.repositiryImpl.MovieRepositoryImpl
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.presentation.utils.AndroidResourceProvider
import javax.inject.Inject
import javax.inject.Singleton


@Module
abstract class DomainModule {

    @Singleton
    @Binds
    abstract fun provideRepository(repository: MovieRepositoryImpl) : MovieRepository
}