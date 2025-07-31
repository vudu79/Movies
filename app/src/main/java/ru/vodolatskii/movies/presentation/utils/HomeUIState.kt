package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie


sealed class HomeUIState {
    data object Loading : HomeUIState()
    data class Success(
        val movies: List<Movie>,
        val hasMore: Boolean,
        val nextPageSize: Int,
        val currentPage: Int,
        val totalPages: Int,
        val totalItems: Int
    ) : HomeUIState()

    data class Error(val message: String) : HomeUIState()
}