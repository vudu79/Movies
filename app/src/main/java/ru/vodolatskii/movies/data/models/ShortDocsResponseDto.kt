package ru.vodolatskii.movies.data.models

data class ShortDocsResponseDto(
    val docs: List<Doc> = emptyList(),
    val total: Long = 0L,
    val limit: Long = 0L,
    val page: Long = 0L,
    val pages: Long = 0L,
)

data class Doc(
    val id: Long = 0L,
    val name: String = "",
    val description: String? = "",
    val poster: Poster,
)

data class Poster(
    val url: String = "",
    val previewUrl: String = "",
)
