package ru.vodolatskii.remote_module

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.vodolatskii.remote_module.entity.Constant
import ru.vodolatskii.remote_module.entity.TMDBPopularMoviesRespDto

interface TmdbApiService {
    @Headers(
        "Authorization: ${Constant.API_KEY_TMDB}",
        "accept: application/json"
    )
    @GET("3/movie/{category}")
    suspend fun getMovie(
        @Path("category") category: String,
        @Query("page") page: Int,
        @Query("language") language: String,
    ): Response<TMDBPopularMoviesRespDto>
}

