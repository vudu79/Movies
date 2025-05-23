package ru.vodolatskii.movies.data.repository

import ru.vodolatskii.movies.data.models.ResponsePostersDto

interface Repository {
    suspend fun getMovieInfo(): ResponsePostersDto?
}