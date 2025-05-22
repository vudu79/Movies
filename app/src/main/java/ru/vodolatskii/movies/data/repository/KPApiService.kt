package ru.vodolatskii.movies.data.repository

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.vodolatskii.movies.data.models.ResponseDto


//private val headers = mapOf("X-API-KEY" to "E333DYJ-NZ2MMD6-PEDXZVX-JYM08SR", "accept" to "application/json")

interface KPsApiService {
    @Headers(
        "X-API-KEY:E333DYJ-NZ2MMD6-PEDXZVX-JYM08SR",
        "Accept:application/json",
//        "User-Agent:PostmanRuntime/7.29.0",
//        "Accept:*/*",
//        "Accept-Encoding:gzip, deflate, br",
//        "Connection:keep-alive"
    )
    @GET("search")
    suspend fun getSearchResponse(
//        @HeaderMap headers: Map<String,String>,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String
    ): Response<ResponseDto>
}

