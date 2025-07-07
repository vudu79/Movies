package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie


sealed class UIState() {
    data class Success(val listMovie: Pair<List<Movie>,List<Movie>>, val isSourceApe :Boolean = true) : UIState()
    object Loading : UIState()
    data class Error(val message: String) :
        UIState()
}