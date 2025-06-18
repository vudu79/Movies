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
import ru.vodolatskii.movies.common.Constant
import ru.vodolatskii.movies.data.service.KPApiService
import ru.vodolatskii.movies.data.service.TmdbApiService
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Tmdb


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

    //    кинопоиск
    @Singleton
    @Provides
    fun provideRetrofitKP(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .baseUrl(Constant.BASE_URL_KP)
        .build()

    @Singleton
    @Provides
    fun provideKPService(retrofit: Retrofit): KPApiService =
        retrofit.create(KPApiService::class.java)

    //TMDB
    @Tmdb
    @Singleton
    @Provides
    fun provideRetrofitTMDB(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .baseUrl(Constant.BASE_URL_TMDB)
        .build()

    @Singleton
    @Provides
    fun provideKPServiceTMDB(@Tmdb retrofit: Retrofit): TmdbApiService =
        retrofit.create(TmdbApiService::class.java)
}