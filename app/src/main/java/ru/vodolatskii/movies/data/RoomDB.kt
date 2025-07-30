package ru.vodolatskii.movies.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.vodolatskii.movies.data.dao.MovieDao
import ru.vodolatskii.movies.data.entity.GenreConverter
import ru.vodolatskii.movies.data.entity.MovieEntity


@Database(entities = [MovieEntity::class], version = 1)
@TypeConverters(GenreConverter::class)
abstract class RoomDB : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "films.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}