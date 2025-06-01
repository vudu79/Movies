package ru.vodolatskii.movies.data.repository.interfaces

import ru.vodolatskii.movies.data.dto.ShortDocsResponseDto
import ru.vodolatskii.movies.data.entity.Movie

interface Repository {
    suspend fun getMovieInfo(): ShortDocsResponseDto?

    suspend fun insertDocIntoDB(doc: Movie)

    suspend fun getAllDocsFromDB(): List<Movie>
}