package ru.vodolatskii.movies.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.vodolatskii.movies.data.dto.Doc
import ru.vodolatskii.movies.data.entity.FavoriteDocs
import ru.vodolatskii.movies.data.repository.interfaces.DocsDao


@Database(entities = [FavoriteDocs::class], version = 1)
abstract class RoomDB : RoomDatabase() {

    abstract fun docsDao(): DocsDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "docs_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}