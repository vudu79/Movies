package ru.vodolatskii.movies.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "favorite_movie")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val movieId: Long = 0,
    val title: String = "",
    val description: String = "",
    val posterUrl: String = "",
    val rating: Double = 0.0,
    val releaseDate: String = "",
    val releaseDateTimeStump: Long = 0L,
    var isFavorite: Boolean = false
) : Parcelable