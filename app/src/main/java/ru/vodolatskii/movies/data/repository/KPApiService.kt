package ru.vodolatskii.movies.data.repository

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.vodolatskii.movies.data.models.ResponsePostersDto

interface KPsApiService {
    @Headers(
//        "X-API-KEY:${BuildConfig.API_KEY}",
        "X-API-KEY:E333DYJ-NZ2MMD6-PEDXZVX-JYM08SR", // смотрите, не жалко))) поменяю потом
        "Accept:application/json",
    )
    @GET("search")
    suspend fun getSearchResponse(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("selectFields") selectFields: List<String>,
        @Query("notNullFields") notNullFields: List<String>
    ): Response<ResponsePostersDto>
}