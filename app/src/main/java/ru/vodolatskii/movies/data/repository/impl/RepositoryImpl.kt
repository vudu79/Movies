package ru.vodolatskii.movies.data.repository.impl

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.dto.ShortDocsResponseDto
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.repository.interfaces.KPsApiService
import ru.vodolatskii.movies.data.repository.interfaces.MovieDao
import ru.vodolatskii.movies.data.repository.interfaces.Repository
import java.util.concurrent.TimeUnit

class RepositoryImpl() : Repository {

    private val movieDao: MovieDao = App.instance.db.movieDao()

    private val BASE_URL_KP =
        "https://api.kinopoisk.dev/v1.4/movie/"

//    private val BASE_URL_TMDB = "https://api.themoviedb.org/3/"

//    private val interceptor = run {
//        val httpLoggingInterceptor = HttpLoggingInterceptor()
//        httpLoggingInterceptor.apply {
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        }
//    }
    private val okHttpClient = OkHttpClient.Builder()
//        .addNetworkInterceptor(interceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder() // adapter
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL_KP)
        .client(okHttpClient)
        .build()

    val service: KPsApiService by lazy {
        retrofit.create(KPsApiService::class.java)
    }

//    private val retrofit = Retrofit.Builder()
////        .addConverterFactory(MoshiConverterFactory.create(moshi))
//        .addConverterFactory(GsonConverterFactory.create())
//        .baseUrl(BASE_URL_TMDB)
//        .client(okHttpClient)
//        .build()

//    val service: TMDBsApiService by lazy {
//        retrofit.create(TMDBsApiService::class.java)
//    }

    override suspend fun getPopularMovieInfo(): ShortDocsResponseDto? {
        val response = service.getSearchResponse(
            1,
            5,
            selectFields = listOf("id", "name", "description", "poster"),
            notNullFields = listOf("name", "poster.url")
        )

        if (response.code() != 200) {
            return null
        } else {
            return response.body()
        }
    }

//    override suspend fun getPopularMovieInfo(): TMDBPopularMoviesRespDto? {
//        val response = service.getSearchResponse(
//            "week",
//            "en-US",
//        )
//        if (response.code() != 200) {
//            return null
//        } else {
//            return response.body()
//        }
//    }

    override suspend fun insertMovieToFavorites(movie: Movie) {
        movieDao.insert(movie)
    }

    override suspend fun deleteMovieFromFavorites(movie: Movie) {
        movieDao.delete(movie)
    }

    override suspend fun getAllMoviesFromFavorites(): List<Movie>? {
        return movieDao.getAllMovie()
    }
}

object RepositoryProvider {
    private val repository: Repository by lazy {
        RepositoryImpl()
    }

    fun provideRepository(): Repository = repository
}