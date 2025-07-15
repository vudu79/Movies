package ru.vodolatskii.movies.domain

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import ru.vodolatskii.movies.data.entity.MovieWithGenre
import ru.vodolatskii.movies.data.service.BaseError
import ru.vodolatskii.movies.data.service.BaseResponse
import ru.vodolatskii.movies.domain.models.Movie

interface MovieRepository {
    suspend fun getMovieResponseFromKPApi(page: Int):BaseResponse<List<Movie>, BaseError>

    suspend fun getMovieResponseFromTMDBApi(page: Int): BaseResponse<List<Movie>, BaseError>

    suspend fun updateMovieToFavorite(isFavorite: Boolean, title: String)

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

     fun getAllMoviesFromDB(): Flow<List<Movie>>

    fun getContentSourceFromPreferences(): String?

    fun saveContentSourceFromPreferences(source: String)

    fun getMovieSavingMode(): Boolean

    fun saveMovieSavingMode(checked: Boolean)

    fun getRatingMovieSavingMode(): Int

    fun saveRatingMovieSavingMode(value: Int)

    fun getDateMovieSavingMode(): Int

    fun saveDateMovieSavingMode(value: Int)

    fun deleteAllFromDB()

     fun getMovieCount(): Int

    suspend fun getMoviesByFilter(
        rating: Double,
        date: Int,
        title: String,
        genres: List<Int>
    ): List<Movie>
}