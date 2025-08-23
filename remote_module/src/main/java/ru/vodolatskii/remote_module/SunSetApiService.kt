package ru.vodolatskii.remote_module

import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.vodolatskii.remote_module.entity.SunSetDto


//https://api.sunrise-sunset.org/json?lat=36.7201600&lng=-4.4203400&date=2024-01-22

interface SunSetApiService {
    @Headers(
        "Accept:application/json",
    )
    @GET("/json")
    fun getSunData(
        @Query("lat") lat: Double,
        @Query("lng") long: Double,
        @Query("date") date: String,
    ): Single<Response<SunSetDto>>
}

