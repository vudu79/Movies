package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie


sealed class UIStateStorage() {
    data class Success(val listMovie: List<Movie> = emptyList()) : UIStateStorage()
    object Loading : UIStateStorage()
    data class Error(val message: String) :
        UIStateStorage()
}