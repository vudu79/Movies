package ru.vodolatskii.movies.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import ru.vodolatskii.movies.domain.models.Movie
import java.util.stream.Collectors


class GenreConverter {
    @TypeConverter
    fun fromGenres(genres: List<String>): String {
        return genres.stream().collect(Collectors.joining(":"))
    }

    @TypeConverter
    fun toGenre(genre: String): List<String> {
        return listOf(*genre.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray())
    }
}

//data class MovieWithGenre(
//    @PrimaryKey(autoGenerate = true)
//    @Embedded val movie: MovieWithoutGenre,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "idGenre"
//    )
//    val genreList: List<Genre>
//)

@Entity(tableName = "movies", indices = [Index(value = ["title"], unique = true)])
data class MovieEntity(
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
    @ColumnInfo(name = "genres") var genres: List<String>,
)

//@Entity(
//    foreignKeys = [ForeignKey(
//        entity = MovieWithoutGenre::class,
//        parentColumns = ["id"],
//        childColumns = ["id_genre_fk"],
//        onDelete = ForeignKey.CASCADE
//    )]
//)
//data class Genre(
//    @PrimaryKey(autoGenerate = true)
//    val idGenre: Long = 0,
//    @ColumnInfo(name = "id_genre_fk") val idGenreFK: Long,
//    @ColumnInfo(name = "genre") val genre: Int
//)

fun MovieEntity.convertEntityToModel(): Movie {
    return Movie(
        apiId = this.apiId,
        title = this.title,
        description = this.description,
        posterUrl = this.posterUrl,
        rating = this.rating,
        releaseDate = this.releaseDate,
        releaseDateTimeStump = this.releaseDateTimeStump,
        releaseDateYear = this.releaseDateYear,
        isFavorite = this.isFavorite,
        genreListString = this.genres
    )
}