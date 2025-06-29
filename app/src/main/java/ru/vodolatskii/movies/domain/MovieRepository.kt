package ru.vodolatskii.movies.domain

import android.content.SharedPreferences
import ru.vodolatskii.movies.data.dto.TMDBPopularMoviesRespDto
import ru.vodolatskii.movies.data.entity.MovieWithGenre
import ru.vodolatskii.movies.data.service.BaseError
import ru.vodolatskii.movies.data.service.BaseResponse
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel

interface MovieRepository {
    suspend fun getMovieResponseFromKPApi(page: Int, callback: MoviesViewModel.ApiCallback)

    suspend fun getMovieResponseFromTMDBApi(page: Int): BaseResponse<List<Movie>, BaseError>

    suspend fun insertMovieToFavorites(movie: Movie)

    suspend fun deleteMovieFromFavorites(movie: Movie)

    suspend fun getAllMoviesFromFavorites(): List<MovieWithGenre>?

    fun getDefaultCategoryFromPreferences(): String

    fun saveDefaultCategoryToPreferences(category: String)

    fun getPreference(): SharedPreferences

    fun getRequestLanguageFromPreferences(): String

    fun saveRequestLanguageToPreferences(language: String)


    suspend fun putMovieToDB(movie: Movie)

    suspend fun putMoviesToDB(movies: List<Movie>)

    suspend fun putMovieToDbWithSettings(movie: Movie)

    suspend fun getAllMoviesFromDB(): List<Movie>

    fun getContentSourceFromPreferences(): String?

    fun saveContentSourceFromPreferences(source: String)

    fun getMovieSavingMode(): Boolean

    fun saveMovieSavingMode(checked: Boolean)

    fun getRatingMovieSavingMode(): Int

    fun saveRatingMovieSavingMode(value: Int)

    fun getDateMovieSavingMode(): Int

    fun saveDateMovieSavingMode(value: Int)

    fun deleteAllFromDB()

    suspend fun getMovieCount(): Int

    suspend fun getMoviesByFilter(
        rating: Double,
        date: Int,
        title: String,
        genres: List<Int>
    ): List<Movie>
}