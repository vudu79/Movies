package ru.vodolatskii.movies.domain.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import ru.vodolatskii.movies.data.entity.MovieWithoutGenre

@Parcelize
data class Movie(
    val id: Long = 0,
    val apiId: Long = 0,
    val title: String = "",
    val description: String = "",
    val posterUrl: String = "",
    val rating: Double = 0.0,
    val releaseDate: String = "",
    val releaseDateTimeStump: Long = 0L,
    val releaseDateYear: Int = -1,
    var isFavorite: Boolean = false,
    var genreList: List<Int> = emptyList()
) : Parcelable

fun Movie.convertModelToEntity(): MovieWithoutGenre {
    return MovieWithoutGenre(
        apiId = this.apiId,
        title = this.title,
        description = this.description,
        posterUrl = this.posterUrl,
        rating = this.rating,
        releaseDate = this.releaseDate,
        releaseDateTimeStump = this.releaseDateTimeStump,
        releaseDateYear = this.releaseDateYear,
        isFavorite = this.isFavorite
    )
}