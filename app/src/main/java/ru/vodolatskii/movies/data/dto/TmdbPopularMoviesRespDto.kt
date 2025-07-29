package ru.vodolatskii.movies.data.dto

import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


data class TMDBPopularMoviesRespDto(
    @Json(name = "page")
    val page: Int,
    @Json(name = "results")
    val tmdbFilms: List<Result>,
    @Json(name = "total_pages")
    val totalPages: Int,
    @Json(name = "total_results")
    val totalResults: Int
)

data class Result(
//    @Json(name ="adult")
//    val adult: Boolean,
//    @Json(name ="backdrop_path")
//    val backdropPath: String,
    @Json(name = "genre_ids")
    val genreIds: List<Int>?,
    @Json(name = "id")
    val id: Int?,
//    @Json(name ="original_language")
//    val originalLanguage: String,
    @Json(name = "original_title")
    val originalTitle: String?,
    @Json(name = "overview")
    val overview: String?,
//    @Json(name ="popularity")
//    val popularity: Double,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "release_date")
    val releaseDate: String?,
    @Json(name = "title")
    val title: String?,
//    @Json(name ="video")
//    val video: Boolean,
    @Json(name = "vote_average")
    val voteAverage: Double?,
//    @Json(name ="vote_count")
//    val voteCount: Int
)


private fun getTimeStump(dateString: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val date = dateFormat.parse(dateString)
    val calendar = Calendar.getInstance()
    date?.let { calendar.setTime(it) }
    return calendar.timeInMillis
}

private fun getYear(dateString: String): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val date = dateFormat.parse(dateString)
    val calendar = Calendar.getInstance()
    date?.let { calendar.setTime(it) }
    return calendar.get(Calendar.YEAR)
}
//
//fun TMDBPopularMoviesRespDto.toMovieList(): MutableList<Movie> {
//    val notNullList = this.tmdbFilms.filter {
//        it.title != null &&
//                it.id != null &&
//                it.releaseDate != null &&
//                it.voteAverage != null &&
//                it.posterPath != null &&
//                it.originalTitle != null &&
//                it.genreIds != null
//    }
//    val movieList: List<Movie> = notNullList.map {
//        val movie = Movie(
//            apiId = it.id!!.toLong(),
//            title = it.title!!,
//            description = it.overview!!,
//            posterUrl = Constant.IMAGES_URL + "original" + it.posterPath,
//            isFavorite = false,
//            rating = it.voteAverage!!,
//            releaseDate = it.releaseDate!!,
//            genreList = it.genreIds!!,
//            releaseDateTimeStump = getTimeStump(it.releaseDate),
//            releaseDateYear = getYear(it.releaseDate)
//        )
//        movie
//    }
//    return movieList.toMutableList()
//}

fun List<Int>.toGenresString(): String {
    var result = ""
    val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Science Fiction",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )
    this.forEach {
        result += "${genreMap.get(it)}, "
    }
    return result
}

