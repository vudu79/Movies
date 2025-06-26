package ru.vodolatskii.movies.presentation.utils

data class StorageSearchEvents (
    val rating: String,
    val date: String,
    val title: String,
    val genres : List<Int>
)