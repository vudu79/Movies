package ru.vodolatskii.remote_module.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class KPResponseDto(
    val docs: List<Doc> = emptyList(),
    val total: Int,
    val limit: Int,
    val page: Int,
    val pages: Int
)

@Parcelize
data class Doc(
    val id: Long,
    val name: String,
    val year: Long,
    val description: String,
    val rating: Rating?,
    val poster: Poster?,
    val genres: List<Genre>,
    val premiere: Premiere?,
) : Parcelable

@Parcelize
data class Genre(
    val name: String?,
) : Parcelable

@Parcelize
data class Poster(
    val url: String? = "",
    val previewUrl: String? = "",
) : Parcelable

@Parcelize
data class Premiere(
    val world: String?,
) : Parcelable

@Parcelize
data class Rating(
    val imdb: Double?,
) : Parcelable

//
//fun KPResponseDto.toMovieList(): List<Movie> {
//    val movieList: List<Movie> = this.docs.map {
//        val dateString = it.premiere?.world?.substring(0, it.premiere.world.indexOf("T")) ?: ""
//        val movie = Movie(
//            apiId = it.id,
//            title = it.name,
//            description = it.description,
//            posterUrl = it.poster?.url ?: "",
//            rating = it.rating?.imdb ?: 0.0,
//            releaseDate = dateString,
//            genreListString = it.genres.map { genre ->
//                if (!genre.name.isNullOrEmpty()) genre.name else ""
//            },
//            releaseDateTimeStump = getTimeStump(dateString),
//            releaseDateYear = it.year.toInt(),
//            isFavorite = false,
//        )
//        movie
//    }
//    return movieList.toMutableList()
//}
//
//private fun getTimeStump(dateString: String): Long {
//    if (dateString.isNotBlank()){
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//        val date = dateFormat.parse(dateString)
//        val calendar = Calendar.getInstance()
//        date?.let { calendar.setTime(it) }
//        return calendar.timeInMillis
//    } else return -1L
//}
//
