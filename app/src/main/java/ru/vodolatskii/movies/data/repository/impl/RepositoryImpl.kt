package ru.vodolatskii.movies.data.repository.impl

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.RoomDB
import ru.vodolatskii.movies.data.dto.Doc
import ru.vodolatskii.movies.data.dto.ShortDocsResponseDto
import ru.vodolatskii.movies.data.entity.FavoriteDocs
import ru.vodolatskii.movies.data.repository.interfaces.DocsDao
import ru.vodolatskii.movies.data.repository.interfaces.KPsApiService
import ru.vodolatskii.movies.data.repository.interfaces.Repository
import java.util.concurrent.TimeUnit

class RepositoryImpl() : Repository {

    private val docsDao: DocsDao = App.instance.db.docsDao()

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

    override suspend fun insertDocIntoDB(doc: FavoriteDocs) {
        docsDao.insert(doc)
    }

    override suspend fun getAllDocsFromDB(): List<FavoriteDocs> {
        return docsDao.getAllDocs()
    }
}

object RepositoryProvider {
    private val repository: Repository by lazy {
        RepositoryImpl()
    }

    fun provideRepository(): Repository = repository
}