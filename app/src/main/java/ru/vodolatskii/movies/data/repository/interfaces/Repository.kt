package ru.vodolatskii.movies.data.repository.interfaces

import ru.vodolatskii.movies.data.dto.ShortDocsResponseDto
import ru.vodolatskii.movies.data.dto.TMDBPopularMoviesRespDto
import ru.vodolatskii.movies.data.entity.Movie

interface Repository {
//    suspend fun getPopularMovieInfo(): ShortDocsResponseDto?
    suspend fun getPopularMovieInfo(): ShortDocsResponseDto?

    suspend fun insertMovieToFavorites(movie: Movie)

    suspend fun deleteMovieFromFavorites(movie: Movie)

    suspend fun getAllMoviesFromFavorites(): List<Movie>?
}