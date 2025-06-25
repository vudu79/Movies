package ru.vodolatskii.movies.data.repositiryImpl

import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import com.google.gson.Gson
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.SQLDatabaseHelper
import ru.vodolatskii.movies.data.dao.MovieDao
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.entity.dto.ErrorResponseDto
import ru.vodolatskii.movies.data.entity.dto.toMovieList
import ru.vodolatskii.movies.data.service.KPApiService
import ru.vodolatskii.movies.data.service.TmdbApiService
import ru.vodolatskii.movies.data.sharedPref.PreferenceProvider
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel
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

    override fun putToDb(movie: Movie) {
        val cv = ContentValues()
        cv.apply {
            put(SQLDatabaseHelper.COLUMN_TITLE, movie.title)
            put(SQLDatabaseHelper.COLUMN_POSTER, movie.posterUrl)
            put(SQLDatabaseHelper.COLUMN_DESCRIPTION, movie.description)
            put(SQLDatabaseHelper.COLUMN_RATING, movie.rating)
            put(SQLDatabaseHelper.COLUMN_RELEASE_DATE, movie.releaseDate)
        }
        sqlDb.insert(SQLDatabaseHelper.TABLE_NAME, null, cv)
    }

    override fun getAllFromDB(): List<Movie> {
        cursor = sqlDb.rawQuery("SELECT * FROM ${SQLDatabaseHelper.TABLE_NAME}", null)
        val result = mutableListOf<Movie>()
        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(1)
                val posterUrl = cursor.getString(2)
                val description = cursor.getString(3)
                val releaseDate = cursor.getString(4)
                val rating = cursor.getDouble(5)

                result.add(
                    Movie(
                        posterUrl = posterUrl,
                        rating = rating,
                        releaseDate = releaseDate,
                        isFavorite = false,
                        title = title,
                        description = description
                    )
                )
            } while (cursor.moveToNext())
        }
        return result
    }

    override suspend fun getPopularMovieKPResponse(
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

    override suspend fun getMovieResponceFromTMDBApi(
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
        movieDao.insert(movie)
    }

    override suspend fun deleteMovieFromFavorites(movie: Movie) {
        movieDao.delete(movie)
    }

    override suspend fun getAllMoviesFromFavorites(): List<Movie>? {
        return movieDao.getAllMovie()
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

    override fun getMovieSavingMode()= preferences.getMovieSavingMode()

    override fun saveMovieSavingMode(checked: Boolean) {
        preferences.saveMovieSavingMode(checked)
    }


    override fun getPreference(): SharedPreferences {
        return preferences.getInstance()
    }
}
