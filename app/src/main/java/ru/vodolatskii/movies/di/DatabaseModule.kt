package ru.vodolatskii.movies.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.vodolatskii.movies.data.RoomDB
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDB(context: Context) : RoomDB = Room
        .databaseBuilder(context, RoomDB::class.java, "my-room-database")
        .fallbackToDestructiveMigration()
        .build()
}