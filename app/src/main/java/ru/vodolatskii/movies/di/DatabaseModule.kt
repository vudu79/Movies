package ru.vodolatskii.movies.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.vodolatskii.movies.data.RoomDB
import ru.vodolatskii.movies.data.SQLDatabaseHelper
import ru.vodolatskii.movies.data.dao.MovieDao
import ru.vodolatskii.movies.data.sharedPref.PreferenceProvider
import ru.vodolatskii.movies.presentation.utils.AndroidResourceProvider
import javax.inject.Singleton

@Module
class DatabaseModule() {
//
//    @Provides
//    @Singleton
//    fun provideConverters(): GenreConverter {
//        return GenreConverter() // Or inject dependencies here if needed
//    }

    @Singleton
    @Provides
    fun provideDB(context: Context) : RoomDB = Room
        .databaseBuilder(context, RoomDB::class.java, "films_room.db")
//        .addTypeConverter(genreConverter)
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideMovieDao(db:RoomDB) : MovieDao = db.movieDao()

    @Singleton
    @Provides
    fun provideSharedPreference(context: Context) = PreferenceProvider(context)

    @Singleton
    @Provides
    fun provideSqlDatabaseHelper(context: Context) = SQLDatabaseHelper(context)

    @Singleton
    @Provides
    fun provideResourceProvider(context: Context) = AndroidResourceProvider(context)

}