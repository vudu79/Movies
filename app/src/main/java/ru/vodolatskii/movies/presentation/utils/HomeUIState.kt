package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie


data class HomeUIState(
    var movies: Pair<List<Movie>, List<Movie>> = Pair(emptyList(), emptyList()),
    var isSourceApe: Boolean = true,
    var isLoading: Boolean = false,
    var error: String = ""
)