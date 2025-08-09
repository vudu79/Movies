package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie

data class MetaWrapper(
    val movies: List<Movie> = emptyList(),
    val total: Int = 0,
    val limit: Int = 0,
    val page: Int = 0,
    val pages: Int = 0,
    val source: SOURCE = SOURCE.WEB
)

enum class SOURCE {
    WEB, BASE
}
