package ru.vodolatskii.movies.data.entity.dto

data class ErrorResponseDto(
    val message: String,
    val error: String,
    val statusCode: Long,
)
