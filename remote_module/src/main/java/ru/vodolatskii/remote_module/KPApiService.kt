package ru.vodolatskii.remote_module

import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.vodolatskii.remote_module.entity.Constant
import ru.vodolatskii.remote_module.entity.KPResponseDto

interface KPApiService {
    @Headers(
        "X-API-KEY:${Constant.API_KEY_KP}",
        "Accept:application/json",
    )
    @GET("v1.4/movie")
    fun getPopularMovieResponse(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("rating.kp") ratingKp: String,
//        @Query("rating.imdb") ratingImdb: String,
        @Query("selectFields") selectFields: List<String>,
        @Query("notNullFields") notNullFields: List<String>
    ): Single<Response<KPResponseDto>>


    @Headers(
        "X-API-KEY:${Constant.API_KEY_KP}",
        "Accept:application/json",
    )
    @GET("v1.4/movie/search")
    fun getSearchResponse(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String,
    ): Single<Response<KPResponseDto>>
}

