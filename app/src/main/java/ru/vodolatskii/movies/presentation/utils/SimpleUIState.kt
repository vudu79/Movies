package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie


sealed class SimpleUIState() {
    data class Success(val listMovie: List<Movie>) : SimpleUIState()
    object Loading : SimpleUIState()
    data class Error(val message: String) : SimpleUIState()
}