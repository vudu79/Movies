package ru.vodolatskii.movies.domain.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Long = 0,
    val apiId: Long = 0,
    val title: String = "",
    val description: String = "",
    val posterUrl: String = "",
    val rating: Double = 0.0,
    val releaseDate: String = "",
    val releaseDateTimeStump: Long = 0L,
    val releaseDateYear: Int = -1,
    var isFavorite: Boolean = false,
    var genreList: List<Int> = emptyList()
) : Parcelable