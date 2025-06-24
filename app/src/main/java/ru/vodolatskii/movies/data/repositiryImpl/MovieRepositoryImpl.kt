package ru.vodolatskii.movies.data.repositiryImpl

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import ru.vodolatskii.movies.App
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
    private val preferences: PreferenceProvider
) : MovieRepository {

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

    override suspend fun getPopularMovieTMDBResponse(
        page: Int,
        callback: MoviesViewModel.ApiCallback
    ) {
        val response = tmdbApiService.getSearchResponse(
            category = getDefaultCategoryFromPreferences(),
            page = page,
            language = "ru-RU",
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


    override  fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    override fun getPreference(): SharedPreferences {
       return preferences.getInstance()
    }

    override fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()
}
