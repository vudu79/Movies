package ru.vodolatskii.movies.domain

import android.content.SharedPreferences
import ru.vodolatskii.movies.data.entity.MovieWithGenre
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel

interface MovieRepository {
    suspend fun getMovieResponseFromKPApi(page: Int, callback: MoviesViewModel.ApiCallback)

    suspend fun getMovieResponseFromTMDBApi(page: Int, callback: MoviesViewModel.ApiCallback)

    suspend fun insertMovieToFavorites(movie: Movie)

    suspend fun deleteMovieFromFavorites(movie: Movie)

    suspend fun getAllMoviesFromFavorites(): List<MovieWithGenre>?

    fun getDefaultCategoryFromPreferences(): String

    fun saveDefaultCategoryToPreferences(category: String)

    fun getPreference(): SharedPreferences

    fun getRequestLanguageFromPreferences(): String

    fun saveRequestLanguageToPreferences(language: String)

    fun putMovieToDbWithSettings(movie: Movie)

    fun getAllFromDB(): List<Movie>

    fun getContentSourceFromPreferences(): String?

    fun saveContentSourceFromPreferences(source: String)

    fun getMovieSavingMode(): Boolean

    fun saveMovieSavingMode(checked: Boolean)

    fun getRatingMovieSavingMode(): Int

    fun saveRatingMovieSavingMode(value: Int)

    fun getDateMovieSavingMode(): Int

    fun saveDateMovieSavingMode(value: Int)

    fun deleteAllFromDB()

    fun getMovieCount():Int

    fun getAllFromDBByFilter(rating: Double, date: Int, title: String, genres: List<Int>): List<Movie>
}