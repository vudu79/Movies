package ru.vodolatskii.movies.presentation.utils

sealed class SortEvents {
    data object DATE: SortEvents()
    data object ALPHABET: SortEvents()
    data object RATING: SortEvents()
}