package ru.vodolatskii.movies.data.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.vodolatskii.movies.common.Constant
import ru.vodolatskii.movies.data.dto.KPResponseDto

interface KPApiService {
    @Headers(
        "X-API-KEY:${Constant.API_KEY_KP}",
        "Accept:application/json",
    )
    @GET("v1.4/movie")
    suspend fun getSearchResponse(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("rating.kp") ratingKp: String,
//        @Query("rating.imdb") ratingImdb: String,
        @Query("selectFields") selectFields: List<String>,
        @Query("notNullFields") notNullFields: List<String>
    ): Response<KPResponseDto>
}

