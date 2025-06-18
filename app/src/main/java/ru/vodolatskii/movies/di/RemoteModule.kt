package ru.vodolatskii.movies.di

import com.github.ajalt.timberkt.BuildConfig
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.vodolatskii.movies.data.service.KPApiService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class RemoteModule {
    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        })
        .build()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder() // adapter
        .add(KotlinJsonAdapterFactory())
        .build()


    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .baseUrl("https://api.kinopoisk.dev/v1.4/movie/")
        .build()

    @Singleton
    @Provides
    fun provideKPService(retrofit: Retrofit): KPApiService =
        retrofit.create(KPApiService::class.java)
}