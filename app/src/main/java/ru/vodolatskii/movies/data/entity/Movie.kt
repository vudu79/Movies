package ru.vodolatskii.movies.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
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

@Entity(tableName = "favorite_movie")
data class MovieWithoutGenre(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val apiId: Long = 0,
    val title: String = "",
    val description: String = "",
    val posterUrl: String = "",
    val rating: Double = 0.0,
    val releaseDate: String = "",
    val releaseDateTimeStump: Long = 0L,
    var isFavorite: Boolean = false,
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = MovieWithoutGenre::class,
        parentColumns = ["id"],
        childColumns = ["idGenreFK"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Genre(
    @PrimaryKey(autoGenerate = true)
    val idGenre: Long = 0,
    val idGenreFK: Long,
    val genre: String
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
        isFavorite = false,
        genreList = this.genreList.map { it.genre }
    )
}