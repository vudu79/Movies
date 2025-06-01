package ru.vodolatskii.movies.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize



data class ShortDocsResponseDto(
    val docs: List<Doc> = emptyList(),
    val total: Long = 0L,
    val limit: Long = 0L,
    val page: Long = 0L,
    val pages: Long = 0L,
)

@Parcelize
data class Doc(
    val id: Long = 0L,
    val name: String = "",
    val description: String? = "",
    val poster: Poster,
) : Parcelable

@Parcelize
data class Poster(
    val url: String = "",
    val previewUrl: String = "",
) : Parcelable
