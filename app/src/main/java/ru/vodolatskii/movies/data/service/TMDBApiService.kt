package ru.vodolatskii.movies.data.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.vodolatskii.movies.common.Constant
import ru.vodolatskii.movies.data.entity.dto.TMDBPopularMoviesRespDto

interface TMDBsApiService {
    @Headers(
        "Authorization: ${Constant.API_KEY_TMDB}",
        "accept: application/json"
    )
    @GET("trending/movie/{time_window}/")
    suspend fun getSearchResponse(
        @Path("time_window") timeWindow: String,
        @Query("language") language: String,
    ): Response<TMDBPopularMoviesRespDto>
}

