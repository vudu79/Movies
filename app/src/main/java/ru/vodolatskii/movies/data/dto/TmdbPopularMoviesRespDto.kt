package ru.vodolatskii.movies.data.dto

import com.google.gson.annotations.SerializedName
import ru.vodolatskii.movies.data.entity.Movie



data class TMDBPopularMoviesRespDto(
    val page: Long,
    val results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Long,
    @SerializedName("total_results")
    val totalResults: Long,
)

data class Result(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("video")
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
)


fun TMDBPopularMoviesRespDto.toMovieList(): MutableList<Movie> {
    val movieList: List<Movie> = this.results.map {
        val movie = Movie(
            movieId = 0L,
            name = it.title,
            description = it.overview,
            posterUrl = it.posterPath,
            isFavorite = false
        )
        movie
    }
    return movieList.toMutableList()
}