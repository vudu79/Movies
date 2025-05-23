package ru.vodolatskii.movies.data.repository

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.vodolatskii.movies.data.models.ResponsePostersDto

class RepositoryImpl() : Repository {

    private val BASE_URL =
        "https://api.kinopoisk.dev/v1.4/movie/"

    private val moshi = Moshi.Builder() // adapter
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    val service: KPsApiService by lazy {
        retrofit.create(KPsApiService::class.java)
    }

    override suspend fun getMovieInfo(): ResponsePostersDto? {
        val response = service.getSearchResponse(
            1,
            100,
            selectFields = listOf("id", "name", "description", "poster"),
            notNullFields = listOf("name", "poster.url")
        )

        if (response.code() != 200) {
            return null
        } else {
            return response.body()
        }
    }
}