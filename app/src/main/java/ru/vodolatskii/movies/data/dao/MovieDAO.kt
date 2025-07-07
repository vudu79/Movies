package ru.vodolatskii.movies.data.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.vodolatskii.movies.data.entity.Genre
import ru.vodolatskii.movies.data.entity.MovieWithGenre
import ru.vodolatskii.movies.data.entity.MovieWithoutGenre
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.domain.models.convertModelToEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieWithoutGenre(movie: MovieWithoutGenre): Long

    @Insert
    suspend fun insertGenres(genre: List<Genre>)

    @Transaction
    suspend fun insertMovie(movie: Movie) {
        val movieId = insertMovieWithoutGenre(movie.convertModelToEntity())
        val genres = movie.genreList.map { Genre(idGenreFK = movieId, genre = it) }
        insertGenres(genres)
    }

    @Transaction
    suspend fun insertMovies(movies: List<Movie>) {
        movies.forEach {
            val movieId = insertMovieWithoutGenre(it.convertModelToEntity())
            val genres = it.genreList.map { Genre(idGenreFK = movieId, genre = it) }
            insertGenres(genres)
        }
    }

    @Query("SELECT * FROM movies")
    fun getAllMovies(): LiveData<List<MovieWithGenre>>

    @Query(
        "SELECT * FROM movies WHERE (:rating = 0.0 OR " +
                "rating >= :rating) AND (:year = 0 OR release_date_year = :year)"
    )
    suspend fun getMoviesByRatingByYear(rating: Double, year: Int): List<MovieWithGenre>

    @Query("UPDATE movies SET is_favorite = :isFavorite WHERE title = :title")
    suspend fun updateMovieToFavorite(isFavorite: Boolean, title: String)

    @Query("DELETE FROM movies WHERE title = :title")
    suspend fun deleteMovieWithoutGenre(title: String)

    @Transaction
    suspend fun deleteMovie(movie: Movie) {
        deleteMovieWithoutGenre(movie.title)
    }

    @Query("SELECT * FROM movies WHERE is_favorite = 1")
    fun getFavoriteMovies(): List<MovieWithGenre>?

    @Query("SELECT COUNT(*) FROM movies")
    fun getCountMovies(): Int
}

