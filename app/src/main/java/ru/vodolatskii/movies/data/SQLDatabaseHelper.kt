package ru.vodolatskii.movies.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SQLDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        //Создаем саму таблицу для фильмов
        db?.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_TITLE TEXT UNIQUE," +
                    "$COLUMN_POSTER TEXT," +
                    "$COLUMN_DESCRIPTION TEXT," +
                    "$COLUMN_RELEASE_DATE TEXT," +
                    "$COLUMN_TIME_STUMP INTEGER," +
                    "$COLUMN_YEAR INTEGER," +
                    "$COLUMN_RATING REAL);"
        )

        db?.execSQL(
            "CREATE TABLE $TABLE_GENRE_NAME (" +
                    "$COLUMN_GENRE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_GENRE_ID_FK INTEGER REFERENCES $TABLE_NAME," +
                    "$COLUMN_GENRE TEXT);"

        )
    }
    //Миграций мы не предполагаем, поэтому метод пустой
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        //Название самой БД
        private const val DATABASE_NAME = "films.db"
        //Версия БД
        private const val DATABASE_VERSION = 1

        const val TABLE_GENRE_NAME = "genre_films_table"
        const val COLUMN_GENRE_ID = "id_genre"
        const val COLUMN_GENRE_ID_FK = "id_genre_fk"
        const val COLUMN_GENRE = "genre"

        const val TABLE_NAME = "cashed_films_table"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_POSTER = "poster_path"
        const val COLUMN_DESCRIPTION = "overview"
        const val COLUMN_RELEASE_DATE = "release_date"
        const val COLUMN_TIME_STUMP = "time_stump"
        const val COLUMN_YEAR = "year"
        const val COLUMN_RATING = "vote_average"
    }
}