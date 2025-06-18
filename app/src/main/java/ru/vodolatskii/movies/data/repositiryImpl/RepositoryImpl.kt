package ru.vodolatskii.movies.data.repositiryImpl

import com.github.ajalt.timberkt.BuildConfig
import com.google.gson.Gson
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.dao.MovieDao
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.entity.dto.ErrorResponseDto
import ru.vodolatskii.movies.data.entity.dto.toMovieList
import ru.vodolatskii.movies.data.service.KPApiService
import ru.vodolatskii.movies.domain.Repository
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel
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
        .connectTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        })
        .build()

    private val moshi = Moshi.Builder() // adapter
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL_KP)
        .client(okHttpClient)
        .build()

    val service: KPApiService by lazy {
        retrofit.create(KPApiService::class.java)
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

    override suspend fun getPopularMovieApiResponse(page: Int, callback: MoviesViewModel.ApiCallback) {
        val resp = service.getSearchResponse(
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
//            .enqueue(object : Callback<ShortDocsResponseDto> {
//            override fun onResponse(
//                call: Call<ShortDocsResponseDto>,
//                response: Response<ShortDocsResponseDto>
//            ) {
//                val apiResponse = response.body()
//                Log.d("mytag", "repository - $apiResponse")
//
//                if (apiResponse != null) {
//                    callback.onSuccess(apiResponse.toMovieList())
//                }
//            }
//
//            override fun onFailure(call: Call<ShortDocsResponseDto>, t: Throwable) {
//
//                Log.d("mytag", "repository tro - $t")
//
//                callback.onFailure(t)
//            }
//        })
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