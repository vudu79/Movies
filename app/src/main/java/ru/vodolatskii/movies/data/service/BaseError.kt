package ru.vodolatskii.movies.data.service

data class BaseError(
    val message: String,
    val error: String,
    val statusCode: Long,
)
