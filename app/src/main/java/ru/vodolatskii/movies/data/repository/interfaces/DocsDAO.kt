package ru.vodolatskii.movies.data.repository.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.vodolatskii.movies.data.entity.FavoriteDocs

@Dao
interface DocsDao {
    @Insert
    suspend fun insert(doc: FavoriteDocs)

    @Update
    suspend fun update(doc: FavoriteDocs)

    @Delete
    suspend fun delete(doc: FavoriteDocs)

    @Query("SELECT * FROM favorite_docs")
    fun getAllDocs(): List<FavoriteDocs>
}