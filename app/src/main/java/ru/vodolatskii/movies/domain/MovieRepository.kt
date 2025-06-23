package ru.vodolatskii.movies.domain

import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel

interface MovieRepository {
    suspend fun getPopularMovieKPResponse(page: Int, callback: MoviesViewModel.ApiCallback)

    suspend fun getPopularMovieTMDBResponse(page: Int, callback: MoviesViewModel.ApiCallback)

    suspend fun insertMovieToFavorites(movie: Movie)

    suspend fun deleteMovieFromFavorites(movie: Movie)

    suspend fun getAllMoviesFromFavorites(): List<Movie>?

    fun getDefaultCategoryFromPreferences() :String

    fun saveDefaultCategoryToPreferences(category: String)
}