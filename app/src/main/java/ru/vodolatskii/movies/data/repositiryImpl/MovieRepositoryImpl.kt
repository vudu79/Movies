package ru.vodolatskii.movies.data.repositiryImpl

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.dao.MovieDao
import ru.vodolatskii.movies.data.dto.toMovieList
import ru.vodolatskii.movies.data.entity.MovieWithGenre
import ru.vodolatskii.movies.data.entity.convertEntityToModel
import ru.vodolatskii.movies.data.service.BaseError
import ru.vodolatskii.movies.data.service.BaseResponse
import ru.vodolatskii.movies.data.service.KPApiService
import ru.vodolatskii.movies.data.service.TmdbApiService
import ru.vodolatskii.movies.data.sharedPref.PreferenceProvider
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.domain.models.Movie
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val kpApiService: KPApiService,
    private val tmdbApiService: TmdbApiService,
    private val preferences: PreferenceProvider,

    ) : MovieRepository {

    override suspend fun getMovieResponseFromKPApi(
        page: Int,
    ): BaseResponse<List<Movie>, BaseError> {
        val response = kpApiService.getSearchResponse(
            page = page,
            limit = App.instance.loadPopularMoviesLimit,
            ratingKp = "1-10",
            selectFields = listOf(
                "id",
                "name",
                "description",
                "poster",
                "premiere",
                "genres",
                "year",
                "rating"
            ),

            notNullFields = listOf(
                "id",
                "name",
                "description",
                "poster.url",
                "premiere.world",
                "genres.name",
                "year",
                "rating.imdb",
            )
        )

        val body = response.body()

        if (response.code() == 200 && body != null) {
            return BaseResponse.Success(body.toMovieList())
        } else {
            val errorResp: BaseError = Gson().fromJson(
                response.errorBody()?.charStream(),
                BaseError::class.java
            )
            return BaseResponse.Error(errorResp)
        }
    }

    override suspend fun getMovieResponseFromTMDBApi(
        page: Int,
    ): BaseResponse<List<Movie>, BaseError> {
        val response = tmdbApiService.getMovie(
            category = getDefaultCategoryFromPreferences(),
            page = page,
            language = getRequestLanguageFromPreferences(),
        )
        val body = response.body()

        if (response.code() == 200 && body != null) {
            return BaseResponse.Success(body.toMovieList())
        } else {
            val errorResp: BaseError = Gson().fromJson(
                response.errorBody()?.charStream(),
                BaseError::class.java
            )
            return BaseResponse.Error(errorResp)
        }
    }


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

    override fun getAllMoviesFromDB(): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { list ->
            val result = list.map { movie ->
                movie.convertEntityToModel()
            }
            result
        }
    }


    override suspend fun getMoviesByFilter(
        rating: Double,
        date: Int,
        title: String,
        genres: List<Int>
    ): List<Movie> {
        val result = movieDao.getMoviesByRatingByYear(rating, date).map {
            it.convertEntityToModel()
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

    override fun deleteAllFromDB() {
        movieDao.getAllMovies()
    }

    override fun getMovieCount(): Int {
        return movieDao.getCountMovies()
    }

    override suspend fun updateMovieToFavorite(isFavorite: Boolean, title: String) {
        movieDao.updateMovieToFavorite(isFavorite, title)
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


//https://api.kinopoisk.dev/v1.4/movie?page=1&limit=5&selectFields=premiere&selectFields=id&selectFields=name&selectFields=description&selectFields=poster&selectFields=genres&selectFields=year&selectFields=rating&selectFields=persons&selectFields=enName&notNullFields=premiere.world&notNullFields=name&notNullFields=enName&notNullFields=description&notNullFields=year&notNullFields=rating.kp&notNullFields=poster.url&notNullFields=id&notNullFields=premiere.world
//
//https://api.kinopoisk.dev/v1.4/movie/?page=1&limit=30&selectFields=id&selectFields=name&selectFields=description&selectFields=poster&selectFields=premiere&selectFields=genres&selectFields=year&selectFields=rating&selectFields=persons&notNullFields=id&notNullFields=name&notNullFields=description&notNullFields=premiere.world&notNullFields=genres&notNullFields=year&notNullFields=rating.kp&notNullFields=persons&notNullFields=poster.url