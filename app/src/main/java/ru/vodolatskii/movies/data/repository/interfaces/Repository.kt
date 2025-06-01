package ru.vodolatskii.movies.data.repository.interfaces

import ru.vodolatskii.movies.data.dto.Doc
import ru.vodolatskii.movies.data.dto.ShortDocsResponseDto
import ru.vodolatskii.movies.data.entity.FavoriteDocs

interface Repository {
    suspend fun getMovieInfo(): ShortDocsResponseDto?

    suspend fun insertDocIntoDB(doc: FavoriteDocs)

    suspend fun getAllDocsFromDB(): List<FavoriteDocs>
}