package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie


sealed class UIStateHome() {
    data class Success(val listMovie: List<Movie> = emptyList()) : UIStateHome()
    object Loading : UIStateHome()
    data class Error(val message: String) :
        UIStateHome()
}