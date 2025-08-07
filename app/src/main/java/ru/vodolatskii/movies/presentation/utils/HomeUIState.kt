package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie


sealed class HomeUIState {
    data object Loading : HomeUIState()
    data class Success(val movie: List<Movie>) : HomeUIState()
    data class Error(val message: String) : HomeUIState()
}