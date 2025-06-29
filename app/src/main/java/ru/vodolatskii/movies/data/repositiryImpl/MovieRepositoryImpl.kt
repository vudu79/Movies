package ru.vodolatskii.movies.data.repositiryImpl

import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import com.google.gson.Gson
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.SQLDatabaseHelper
import ru.vodolatskii.movies.data.dao.MovieDao
import ru.vodolatskii.movies.data.entity.MovieWithGenre
import ru.vodolatskii.movies.data.entity.convertToModel
import ru.vodolatskii.movies.data.entity.dto.ErrorResponseDto
import ru.vodolatskii.movies.data.entity.dto.toMovieList
import ru.vodolatskii.movies.data.service.KPApiService
import ru.vodolatskii.movies.data.service.TmdbApiService
import ru.vodolatskii.movies.data.sharedPref.PreferenceProvider
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val kpApiService: KPApiService,
    private val tmdbApiService: TmdbApiService,
    private val preferences: PreferenceProvider,
    private val sqlDatabaseHelper: SQLDatabaseHelper

) : MovieRepository {
    private val sqlDb = sqlDatabaseHelper.readableDatabase
    private lateinit var cursor: Cursor


    override suspend fun putMovieToDbWithSettings(movie: Movie) {
        if (getMovieSavingMode()) {
            putMovieToDB(movie)
        } else if (
            movie.rating >= getRatingMovieSavingMode() &&
            movie.releaseDateTimeStump >= getTimeStump(getDateMovieSavingMode())
        ) {
            putMovieToDB(movie)
        }
    }

    override suspend fun putMoviesToDB(movies: List<Movie>) {
        movieDao.insertMovies(movies)
    }

    override suspend fun putMovieToDB(movie: Movie) {
        movieDao.insertMovie(movie)
    }

    override suspend fun getAllMoviesFromDB(): List<Movie> {
        return movieDao.getAllMovies().map { it.convertToModel() }
    }

    override suspend fun getMoviesByFilter(
        rating: Double,
        date: Int,
        title: String,
        genres: List<Int>
    ): List<Movie> {

//        cursor = sqlDb.rawQuery(
//            "SELECT ${SQLDatabaseHelper.TABLE_NAME}.*, ${SQLDatabaseHelper.TABLE_GENRE_NAME}.genre FROM" +
//                    " ${SQLDatabaseHelper.TABLE_NAME} JOIN ${SQLDatabaseHelper.TABLE_GENRE_NAME} ON " +
//                    "${SQLDatabaseHelper.TABLE_GENRE_NAME}.id_genre_fk = ${SQLDatabaseHelper.TABLE_NAME}.id WHERE ($rating = 0.0 OR " +
//                    "${SQLDatabaseHelper.COLUMN_RATING} >= $rating) AND " +
//                    "($date = 0 OR ${SQLDatabaseHelper.COLUMN_YEAR} = $date)", null
//        )
        val result = movieDao.getMoviesByRatingByYear(rating, date).map {
            it.convertToModel()
        }

        if (genres.isNotEmpty()) {
            val genreFilteredList = result.filter {
                val intersection = genres.intersect(it.genreList.toSet())
                genres.containsAll(it.genreList) || intersection.isNotEmpty()
            }
            if (title != "") {
                val titleFilteredList = genreFilteredList.filter {
                    it.title.lowercase().contains(title.lowercase())
                }
                return titleFilteredList
            } else return genreFilteredList

        } else if (title != "") {
            val titleFilteredList = result.filter {
                it.title.lowercase().contains(title.lowercase())
            }
            return titleFilteredList
        } else return result
    }

    override suspend fun deleteAllFromDB() {
        movieDao.getAllMovies()
    }

    override fun getMovieCount(): Int {
        return movieDao.getCountMovies()
    }

    override suspend fun getMovieResponseFromKPApi(
        page: Int,
        callback: MoviesViewModel.ApiCallback
    ) {
        val resp = kpApiService.getSearchResponse(
            page = page,
            limit = App.instance.loadPopularMoviesLimit,
            selectFields = listOf("id", "name", "description", "poster"),
            notNullFields = listOf("name", "poster.url")
        )
        val body = resp.body()

        if (resp.code() == 200 && body != null) {
            callback.onSuccess(body.toMovieList())
        } else {

            val errorResp: ErrorResponseDto = Gson().fromJson(
                resp.errorBody()?.charStream(),
                ErrorResponseDto::class.java
            )
            callback.onFailure(errorResp)
        }
    }

    override suspend fun getMovieResponseFromTMDBApi(
        page: Int,
        callback: MoviesViewModel.ApiCallback
    ) {
        val response = tmdbApiService.getSearchResponse(
            category = getDefaultCategoryFromPreferences(),
            page = page,
            language = getRequestLanguageFromPreferences(),
        )
        val body = response.body()

        if (response.code() == 200 && body != null) {
            callback.onSuccess(body.toMovieList())
        } else {
            val errorResp: ErrorResponseDto = Gson().fromJson(
                response.errorBody()?.charStream(),
                ErrorResponseDto::class.java
            )
            callback.onFailure(errorResp)
        }
    }

    override suspend fun insertMovieToFavorites(movie: Movie) {
        movieDao.insertMovie(movie)
    }

    override suspend fun deleteMovieFromFavorites(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    override suspend fun getAllMoviesFromFavorites(): List<MovieWithGenre>? {
        return movieDao.getFavoriteMovies()
    }


    override fun getRequestLanguageFromPreferences() = preferences.getRequestLanguage()
    override fun saveRequestLanguageToPreferences(language: String) {
        preferences.saveRequestLanguage(language)
    }


    override fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()
    override fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }


    override fun getContentSourceFromPreferences() = preferences.getContentSource()
    override fun saveContentSourceFromPreferences(source: String) {
        preferences.saveContentSource(source)
    }


    override fun getMovieSavingMode() = preferences.getMovieSavingMode()
    override fun saveMovieSavingMode(checked: Boolean) {
        preferences.saveMovieSavingMode(checked)
    }


    override fun getRatingMovieSavingMode() = preferences.getRatingMovieSavingMode()
    override fun saveRatingMovieSavingMode(value: Int) {
        preferences.saveRatingMovieSavingMode(value)
    }

    override fun getDateMovieSavingMode() = preferences.getDateMovieSavingMode()
    override fun saveDateMovieSavingMode(value: Int) {
        preferences.saveDateMovieSavingMode(value)
    }


    override fun getPreference(): SharedPreferences {
        return preferences.getInstance()
    }

    private fun getTimeStump(dateInt: Int): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date = dateFormat.parse("$dateInt-01-01")
        val calendar = Calendar.getInstance()
        date?.let { calendar.setTime(it) }
        return calendar.timeInMillis
    }
}
