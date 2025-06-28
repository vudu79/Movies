package ru.vodolatskii.movies.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.vodolatskii.movies.data.entity.Genre
import ru.vodolatskii.movies.data.entity.MovieWithGenre
import ru.vodolatskii.movies.data.entity.MovieWithoutGenre
import ru.vodolatskii.movies.domain.models.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieWithoutGenre(movie: MovieWithoutGenre): Long

    @Insert
    suspend fun insertGenres(genre: List<Genre>)

    @Transaction
    suspend fun insertMovieWithGenre(movie: Movie) {
        val movieId = insertMovieWithoutGenre(convertModelToEntity(movie))
        val genres = movie.genreList.map { Genre(idGenreFK = movieId, genre = it) }
        insertGenres(genres)
    }

    @Query("DELETE FROM favorite_movie WHERE title = :title")
    suspend fun deleteMovieWithoutGenre(title: String)

    @Transaction
    suspend fun deleteFavoriteMovie(movie: Movie) {
        deleteMovieWithoutGenre(movie.title)
    }

    @Query("SELECT * FROM favorite_movie")
    fun getAllMovieFromFavorite(): List<MovieWithGenre>?
}

private fun convertModelToEntity(movie: Movie): MovieWithoutGenre {
    return MovieWithoutGenre(
        apiId = movie.apiId,
        title = movie.title,
        description = movie.description,
        posterUrl = movie.posterUrl,
        rating = movie.rating,
        releaseDate = movie.releaseDate,
        releaseDateTimeStump = movie.releaseDateTimeStump,
        isFavorite = false
    )
}
