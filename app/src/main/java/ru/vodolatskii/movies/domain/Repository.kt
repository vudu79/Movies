package ru.vodolatskii.movies.domain

import ru.vodolatskii.movies.data.entity.dto.ShortDocsResponseDto
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel

interface Repository {
    suspend fun getPopularMovieApiResponse(page: Int, callback: MoviesViewModel.ApiCallback)

    suspend fun insertMovieToFavorites(movie: Movie)

    suspend fun deleteMovieFromFavorites(movie: Movie)

    suspend fun getAllMoviesFromFavorites(): List<Movie>?
}