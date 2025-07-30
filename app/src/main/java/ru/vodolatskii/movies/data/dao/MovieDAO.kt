package ru.vodolatskii.movies.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Single
import ru.vodolatskii.movies.data.entity.MovieEntity
import ru.vodolatskii.movies.domain.models.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertMovie(movie: MovieEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Single<List<MovieEntity>>

    @Query(
        "SELECT * FROM movies WHERE (:rating = 0.0 OR " +
                "rating >= :rating) AND (:year = 0 OR release_date_year = :year)"
    )
    suspend fun getMoviesByRatingByYear(rating: Double, year: Int): List<MovieEntity>

    @Query("UPDATE movies SET is_favorite = :isFavorite WHERE title = :title")
     fun updateMovieToFavorite(isFavorite: Boolean, title: String)

    @Query("DELETE FROM movies WHERE title = :title")
    suspend fun deleteMovie(title: String)

    suspend fun deleteMovie(movie: Movie) {
        deleteMovie(movie.title)
    }

    @Query("SELECT * FROM movies WHERE is_favorite = 1")
    fun getFavoriteMovies(): Single<List<MovieEntity>>

    @Query("SELECT COUNT(*) FROM movies")
    fun getCountMovies(): Int
}

