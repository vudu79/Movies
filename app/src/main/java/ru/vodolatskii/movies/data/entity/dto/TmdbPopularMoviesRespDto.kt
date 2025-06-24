package ru.vodolatskii.movies.data.entity.dto

import com.squareup.moshi.Json
import ru.vodolatskii.movies.common.Constant
import ru.vodolatskii.movies.data.entity.Movie


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
//    @Json(name ="genre_ids")
//    val genreIds: List<Int>,
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


fun TMDBPopularMoviesRespDto.toMovieList(): MutableList<Movie> {

    val notNullList = this.tmdbFilms.filter {
        it.title != null &&
                it.id != null &&
                it.releaseDate != null &&
                it.voteAverage != null &&
                it.posterPath != null &&
                it.originalTitle != null
    }

    val movieList: List<Movie> = notNullList.map {
        val movie = Movie(
            movieId = it.id!!.toLong(),
            name = it.title!!,
            description = it.overview!!,
            posterUrl = Constant.IMAGES_URL + "original" + it.posterPath,
            isFavorite = false,
            rating = it.voteAverage!!
        )
        movie
    }
    return movieList.toMutableList()
}