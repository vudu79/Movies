package ru.vodolatskii.movies.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.vodolatskii.movies.domain.models.Movie


data class ShortDocsResponseDto(
    val docs: List<Doc> = emptyList(),
)

@Parcelize
data class Doc(
    val id: Long = 0L,
    val name: String = "",
    val description: String = "",
    val poster: Poster,
    val rating: Rating,
    val genres: List<Genre>,
    ) : Parcelable

@Parcelize
data class Genre(
    val name: String,
) : Parcelable

@Parcelize
data class Poster(
    val url: String = "",
    val previewUrl: String = "",
) : Parcelable


@Parcelize
data class Rating(
    val kp: Double,
    val imdb: Double,
    val filmCritics: Double,
    val russianFilmCritics: Double,
) : Parcelable

fun ShortDocsResponseDto.toMovieList(): MutableList<Movie> {
    val movieList: List<Movie> = this.docs.map {
        val movie = Movie(
            apiId = it.id,
            title = it.name,
            description = it.description,
            posterUrl = it.poster.url,
            isFavorite = false,
            rating = it.rating.kp

        )
        movie
    }
    return movieList.toMutableList()
}