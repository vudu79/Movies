package ru.vodolatskii.movies.data.repository

import ru.vodolatskii.movies.data.models.ResponseDto

interface Repository {
    suspend fun getMovieInfo(): ResponseDto?
}