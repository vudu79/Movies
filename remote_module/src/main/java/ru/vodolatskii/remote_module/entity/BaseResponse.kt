package ru.vodolatskii.remote_module.entity


sealed class BaseResponse<out T, out U> {
    data class Success<out T>(val body: T) : BaseResponse<T, Nothing>()
    data class Error<out U>(val massage: U) : BaseResponse<Nothing, U>()
}