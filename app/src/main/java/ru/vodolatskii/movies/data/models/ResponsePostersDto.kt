package ru.vodolatskii.movies.data.models

data class ResponsePostersDto(
    val docs: List<Doc>,
    val total: Long,
    val limit: Long,
    val page: Long,
    val pages: Long,
)

data class Doc(
    val id: Long,
    val name: String,
    val description: String?,
    val poster: Poster,
)

data class Poster(
    val url: String,
    val previewUrl: String,
)
