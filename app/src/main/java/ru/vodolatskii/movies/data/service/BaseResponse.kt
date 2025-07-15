package ru.vodolatskii.movies.data.service

import ru.vodolatskii.movies.data.dto.TMDBPopularMoviesRespDto

sealed class BaseResponse<out T, out U> {
    data class Success<out T>(val body: T) : BaseResponse<T, Nothing>()
    data class Error<out U>(val massage: U) : BaseResponse<Nothing, U>()
    data object Loading : BaseResponse<Nothing, Nothing>()
}