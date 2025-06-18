package ru.vodolatskii.movies.data.repositiryImpl

import com.google.gson.Gson
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.dao.MovieDao
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.entity.dto.ErrorResponseDto
import ru.vodolatskii.movies.data.entity.dto.toMovieList
import ru.vodolatskii.movies.data.service.KPApiService
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel
import javax.inject.Inject


class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val kpApiService: KPApiService

) : MovieRepository {


//    private val BASE_URL_KP =
//        "https://api.kinopoisk.dev/v1.4/movie/"

//    private val BASE_URL_TMDB = "https://api.themoviedb.org/3/"

    //    private val interceptor = run {
//        val httpLoggingInterceptor = HttpLoggingInterceptor()
//        httpLoggingInterceptor.apply {
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        }
//    }


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

    override suspend fun insertMovieToFavorites(movie: Movie) {
        movieDao.insert(movie)
    }

    override suspend fun deleteMovieFromFavorites(movie: Movie) {
        movieDao.delete(movie)
    }

    override suspend fun getAllMoviesFromFavorites(): List<Movie>? {
        return movieDao.getAllMovie()
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


}
//
//object RepositoryProvider {
//    private val repository: Repository by lazy {
//        RepositoryImpl()
//    }
//
//    fun provideRepository(): Repository = repository
//}