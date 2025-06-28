package ru.vodolatskii.movies.presentation.utils

data class StorageSearchEvent (
    val rating: String ="",
    val date: String = "",
    val title: String = "",
    val genres : List<Int> = emptyList()
)