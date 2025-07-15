package ru.vodolatskii.movies.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.vodolatskii.movies.domain.models.Movie
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


data class KPResponseDto(
    val docs: List<Doc> = emptyList(),
    val total: Long,
    val limit: Long,
    val page: Long,
    val pages: Long
)

@Parcelize
data class Doc(
    val id: Long,
    val name: String,
    val year: Long,
    val description: String,
    val rating: Rating,
    val poster: Poster,
    val genres: List<Genre>,
//    val persons: List<Person>,
    val premiere: Premiere,
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
data class Premiere(
    val world: String,
) : Parcelable

@Parcelize
data class Rating(
//    val kp: Double,
    val imdb: Double,
//    val filmCritics: Long,
//    val russianFilmCritics: Long,
//    val await: Long?,
) : Parcelable


fun KPResponseDto.toMovieList(): List<Movie> {
    val notNullList = this.docs.filter {
        it.id != null && it.description != null && it.premiere.world != null && it.rating.imdb != null && it.poster.url != null && it.genres != null
    }
    val movieList: List<Movie> = notNullList.map {
        val dateString = it.premiere.world.substring(0, it.premiere.world.indexOf("T"))
        val movie = Movie(
            apiId = it.id,
            title = it.name,
            description = it.description,
            posterUrl = it.poster.url,
            rating = it.rating.imdb,
            releaseDate = dateString,
            genreListString = it.genres.map { genre -> genre.name },
            releaseDateTimeStump = getTimeStump(dateString),
            releaseDateYear = it.year.toInt(),
            isFavorite = false,
        )
        movie
    }
    return movieList.toMutableList()
}

private fun getTimeStump(dateString: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val date = dateFormat.parse(dateString)
    val calendar = Calendar.getInstance()
    date?.let { calendar.setTime(it) }
    return calendar.timeInMillis
}

