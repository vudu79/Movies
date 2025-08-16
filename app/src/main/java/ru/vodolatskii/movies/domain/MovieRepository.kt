package ru.vodolatskii.movies.domain

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Single
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.utils.MetaWrapper

interface MovieRepository {
     fun getMovieResponseFromKPApi(page: Int, query: String):Single<MetaWrapper>

//    suspend fun getMovieResponseFromTMDBApi(page: Int): BaseResponse<List<Movie>, BaseError>

//    fun getSearchResponseFromKPApi(page: Int, query: String): Single<List<Movie>>

    fun updateMovieToFavorite(isFavorite: Boolean, title: String)

    suspend fun deleteMovieFromFavorites(movie: Movie)

     fun getAllMoviesFromFavorites(): Single<List<Movie>>

    fun getDefaultCategoryFromPreferences(): String

    fun saveDefaultCategoryToPreferences(category: String)

    fun getPreference(): SharedPreferences

    fun getRequestLanguageFromPreferences(): String

    fun saveRequestLanguageToPreferences(language: String)

    suspend fun putMovieToDB(movie: Movie)

    suspend fun putMoviesToDB(movies: List<Movie>)

    suspend fun putMovieToDbWithSettings(movie: Movie)

     fun getAllMoviesFromDB(): Single<List<Movie>>

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

//    suspend fun getMoviesByFilter(
//        rating: Double,
//        date: Int,
//        title: String,
//        genres: List<Int>
//    ): List<Movie>
}