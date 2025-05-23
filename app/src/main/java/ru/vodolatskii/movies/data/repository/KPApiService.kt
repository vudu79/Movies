package ru.vodolatskii.movies.data.repository

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.vodolatskii.movies.BuildConfig
import ru.vodolatskii.movies.data.models.ResponsePostersDto

interface KPsApiService {
    @Headers(
        "X-API-KEY:${BuildConfig.API_KEY}",
        "Accept:application/json",
//        "User-Agent:PostmanRuntime/7.29.0",
//        "Accept:*/*",
//        "Accept-Encoding:gzip, deflate, br",
//        "Connection:keep-alive"
    )
    @GET("search")
    suspend fun getSearchResponse(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("selectFields") selectFields: List<String>,
        @Query("notNullFields") notNullFields: List<String>
    ): Response<ResponsePostersDto>
}