package ru.vodolatskii.movies.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.vodolatskii.movies.data.entity.Movie


data class ShortDocsResponseDto(
    val docs: List<Doc> = emptyList(),
)

@Parcelize
data class Doc(
    val id: Long = 0L,
    val name: String = "",
    val description: String = "",
    val poster: Poster,
) : Parcelable

@Parcelize
data class Poster(
    val url: String = "",
    val previewUrl: String = "",
) : Parcelable

fun ShortDocsResponseDto.toMovieList(): List<Movie> {
    val movieList: List<Movie> = this.docs.map {
        val movie = Movie( name = it.name, description = it.description, posterUrl = it.poster.url, isFavorite = false)
        movie
    }
    return movieList
}