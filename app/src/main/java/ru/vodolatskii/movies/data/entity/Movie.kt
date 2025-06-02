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
    val name: String,
    val description: String,
    val posterUrl: String,
    var isFavorite: Boolean
): Parcelable