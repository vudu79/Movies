package ru.vodolatskii.movies.data.repositiryImpl

import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.util.Log
import com.google.gson.Gson
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.SQLDatabaseHelper
import ru.vodolatskii.movies.data.dao.MovieDao
import ru.vodolatskii.movies.data.entity.MovieWithGenre
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

    private fun getTimeStump(dateInt: Int): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date = dateFormat.parse("$dateInt-01-01")
        val calendar = Calendar.getInstance()
        date?.let { calendar.setTime(it) }
        return calendar.timeInMillis
    }

    override fun putMovieToDbWithSettings(movie: Movie) {
        if (getMovieSavingMode()) {
            putMovieToDB(movie)
        } else if (
            movie.rating >= getRatingMovieSavingMode() &&
            movie.releaseDateTimeStump >= getTimeStump(getDateMovieSavingMode())
        ) {
            putMovieToDB(movie)
        }
    }

    private fun putMovieToDB(movie: Movie) {
        var movieId = -1L
        sqlDb.beginTransaction()
        try {
            val cv = ContentValues()
            cv.apply {
                put(SQLDatabaseHelper.COLUMN_TITLE, movie.title)
                put(SQLDatabaseHelper.COLUMN_POSTER, movie.posterUrl)
                put(SQLDatabaseHelper.COLUMN_DESCRIPTION, movie.description)
                put(SQLDatabaseHelper.COLUMN_RATING, movie.rating)
                put(SQLDatabaseHelper.COLUMN_RELEASE_DATE, movie.releaseDate)
                put(SQLDatabaseHelper.COLUMN_TIME_STUMP, movie.releaseDateTimeStump)
                put(SQLDatabaseHelper.COLUMN_YEAR, movie.releaseDateYear)
            }
            movieId = sqlDb.insert(SQLDatabaseHelper.TABLE_NAME, null, cv)

            val cvGenre = ContentValues()
            movie.genreList.forEach {
                cvGenre.put(SQLDatabaseHelper.COLUMN_GENRE_ID_FK, movieId)
                cvGenre.put(SQLDatabaseHelper.COLUMN_GENRE, it)
                sqlDb.insertOrThrow(SQLDatabaseHelper.TABLE_GENRE_NAME, null, cvGenre)
            }

            sqlDb.setTransactionSuccessful()
        } catch (e: Exception) {

        } finally {
            sqlDb.endTransaction()
        }
    }

    override fun getAllFromDB(): List<Movie> {
        cursor = sqlDb.rawQuery(
            "SELECT ${SQLDatabaseHelper.TABLE_NAME}.*, ${SQLDatabaseHelper.TABLE_GENRE_NAME}.genre FROM ${SQLDatabaseHelper.TABLE_NAME} JOIN ${SQLDatabaseHelper.TABLE_GENRE_NAME} ON ${SQLDatabaseHelper.TABLE_GENRE_NAME}.id_genre_fk = ${SQLDatabaseHelper.TABLE_NAME}.id",
            null
        )
        val result = mutableListOf<Movie>()
        val resultList = mutableListOf<Movie>()
        if (cursor.moveToFirst()) {
            do {
                val _title = cursor.getString(1)
                val posterUrl = cursor.getString(2)
                val description = cursor.getString(3)
                val releaseDate = cursor.getString(4)
                val timeStump = cursor.getLong(5)
                val year = cursor.getInt(6)
                val _rating = cursor.getDouble(7)
                val genre = cursor.getInt(8)

                result.add(
                    Movie(
                        posterUrl = posterUrl,
                        rating = _rating,
                        releaseDate = releaseDate,
                        isFavorite = false,
                        title = _title,
                        description = description,
                        releaseDateTimeStump = timeStump,
                        releaseDateYear = year,
                        genreList = listOf(genre)
                    )
                )
            } while (cursor.moveToNext())
        }

        val res = result.groupBy {
            it.title
        }

        for (entry in res) {
            val values = entry.value
            val l = values.flatMap {
                it.genreList
            }
            val movie = values[0]
            movie.genreList = l
            resultList.add(movie)
        }
        return resultList
    }

    override fun getAllFromDBByFilter(
        rating: Double,
        date: Int,
        title: String,
        genres: List<Int>
    ): List<Movie> {
        cursor = sqlDb.rawQuery(
            "SELECT ${SQLDatabaseHelper.TABLE_NAME}.*, ${SQLDatabaseHelper.TABLE_GENRE_NAME}.genre FROM" +
                    " ${SQLDatabaseHelper.TABLE_NAME} JOIN ${SQLDatabaseHelper.TABLE_GENRE_NAME} ON " +
                    "${SQLDatabaseHelper.TABLE_GENRE_NAME}.id_genre_fk = ${SQLDatabaseHelper.TABLE_NAME}.id WHERE ($rating = 0.0 OR " +
                    "${SQLDatabaseHelper.COLUMN_RATING} >= $rating) AND " +
                    "($date = 0 OR ${SQLDatabaseHelper.COLUMN_YEAR} = $date)", null
        )

        val result = mutableListOf<Movie>()
        if (cursor.moveToFirst()) {
            do {
                val _title = cursor.getString(1)
                val posterUrl = cursor.getString(2)
                val description = cursor.getString(3)
                val releaseDate = cursor.getString(4)
                val timeStump = cursor.getLong(5)
                val year = cursor.getInt(6)
                val _rating = cursor.getDouble(7)
                val genre = cursor.getInt(8)

                result.add(
                    Movie(
                        posterUrl = posterUrl,
                        rating = _rating,
                        releaseDate = releaseDate,
                        isFavorite = false,
                        title = _title,
                        description = description,
                        releaseDateTimeStump = timeStump,
                        releaseDateYear = year,
                        genreList = listOf(genre),
                    )
                )
            } while (cursor.moveToNext())
        }
        if (genres.isNotEmpty()) {
            val genreFilteredList = result.groupBy {
                it.genreList[0]
            }.filter { genres.contains(it.key) }.values.flatten()
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

    override fun deleteAllFromDB() {
        sqlDb.execSQL("DELETE FROM ${SQLDatabaseHelper.TABLE_NAME}")
    }

    override fun getMovieCount(): Int {
        var count = 0
        val cursor = sqlDb.rawQuery("SELECT COUNT(*) FROM ${SQLDatabaseHelper.TABLE_NAME}", null)
        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        return count
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
        movieDao.insertMovieWithGenre(movie)
    }

    override suspend fun deleteMovieFromFavorites(movie: Movie) {
        movieDao.deleteFavoriteMovie(movie)
    }

    override suspend fun getAllMoviesFromFavorites(): List<MovieWithGenre>? {
        return movieDao.getAllMovieFromFavorite()
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
}
