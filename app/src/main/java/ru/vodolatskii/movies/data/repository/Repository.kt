package ru.vodolatskii.movies.data.repository

import ru.vodolatskii.movies.data.models.ShortDocsResponseDto

interface Repository {
    suspend fun getMovieInfo(): ShortDocsResponseDto?
}