package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie


sealed class FavoriteUIState() {
    data class Success(val listMovie: Pair<List<Movie>,List<Movie>>, val isSourceApe :Boolean = true) : FavoriteUIState()
    object Loading : FavoriteUIState()
    data class Error(val message: String) :
        FavoriteUIState()
}