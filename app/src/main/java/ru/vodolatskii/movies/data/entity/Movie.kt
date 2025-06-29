package ru.vodolatskii.movies.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import ru.vodolatskii.movies.domain.models.Movie


data class MovieWithGenre(
    @PrimaryKey(autoGenerate = true)
    @Embedded val movie: MovieWithoutGenre,
    @Relation(
        parentColumn = "id",
        entityColumn = "idGenre"
    )
    val genreList: List<Genre>
)

@Entity(tableName = "movies", indices = [Index(value = ["title"], unique = true)])
data class MovieWithoutGenre(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "api_id") val apiId: Long = 0,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "poster_url") val posterUrl: String = "",
    @ColumnInfo(name = "rating") val rating: Double = 0.0,
    @ColumnInfo(name = "release_date") val releaseDate: String = "",
    @ColumnInfo(name = "release_date_time_stump") val releaseDateTimeStump: Long = 0L,
    @ColumnInfo(name = "release_date_year") val releaseDateYear: Int = 0,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean = false,
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = MovieWithoutGenre::class,
        parentColumns = ["id"],
        childColumns = ["id_genre_fk"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Genre(
    @PrimaryKey(autoGenerate = true)
    val idGenre: Long = 0,
    @ColumnInfo(name = "id_genre_fk") val idGenreFK: Long,
    @ColumnInfo(name = "genre") val genre: Int
)

fun MovieWithGenre.convertToModel(): Movie {
    return Movie(
        apiId = this.movie.apiId,
        title = this.movie.title,
        description = this.movie.description,
        posterUrl = this.movie.posterUrl,
        rating = this.movie.rating,
        releaseDate = this.movie.releaseDate,
        releaseDateTimeStump = this.movie.releaseDateTimeStump,
        releaseDateYear = this.movie.releaseDateYear,
        isFavorite = false,
        genreList = this.genreList.map { it.genre }
    )
}