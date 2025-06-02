package ru.vodolatskii.movies.data.repository.impl

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.dto.ShortDocsResponseDto
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.repository.interfaces.KPsApiService
import ru.vodolatskii.movies.data.repository.interfaces.MovieDao
import ru.vodolatskii.movies.data.repository.interfaces.Repository
import java.util.concurrent.TimeUnit

class RepositoryImpl() : Repository {

    private val movieDao: MovieDao = App.instance.db.movieDao()

    private val BASE_URL =
        "https://api.kinopoisk.dev/v1.4/movie/"

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder() // adapter
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    val service: KPsApiService by lazy {
        retrofit.create(KPsApiService::class.java)
    }

    override suspend fun getMovieInfo(): ShortDocsResponseDto? {
        val response = service.getSearchResponse(
            1,
            50,
            selectFields = listOf("id", "name", "description", "poster"),
            notNullFields = listOf("name", "poster.url")
        )

        if (response.code() != 200) {
            return null
        } else {
            return response.body()
        }
    }

    override suspend fun insertMovieToFavorites(doc: Movie) {
        movieDao.insert(doc)
    }

    override suspend fun deleteMovieFromFavorites(movie: Movie) {
        movieDao.delete(movie)
    }

    override suspend fun getAllMoviesFromFavorites(): List<Movie>? {
        return movieDao.getAllMovie()
//        return listOf(
//
//            Movie(
//                id = 3877L,
//                name = "Jameelah",
//                description = "Erendira",
//                posterUrl = "https://image.openmoviedb.com/kinopoisk-images/10900341/caf9f155-1a19-42f1-a0f3-9c8773e9083e/orig",
//                isFavorite = true
//            ),
//            Movie(
//                id = 8063L,
//                name = "Malka",
//                description = "Millard",
//                posterUrl = "https://image.openmoviedb.com/kinopoisk-images/1599028/637271d5-61b4-4e46-ac83-6d07494c7645/orig",
//                isFavorite = true
//            ),
//            Movie(
//                id = 3363L,
//                name = "sdfsdfsdf",
//                description = "Milsdsdfsdfsdfsdfsdfslard",
//                posterUrl = "https://image.openmoviedb.com/kinopoisk-images/1898899/5fb7d956-d5fb-4189-9ec9-1a051aaa7f41/orig",
//                isFavorite = true
//            )
//        )
    }
}

object RepositoryProvider {
    private val repository: Repository by lazy {
        RepositoryImpl()
    }

    fun provideRepository(): Repository = repository
}