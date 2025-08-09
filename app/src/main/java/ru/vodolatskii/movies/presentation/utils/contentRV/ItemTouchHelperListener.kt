package ru.vodolatskii.movies.presentation.utils.contentRV

import ru.vodolatskii.movies.domain.models.Movie


interface ItemTouchHelperListener {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDismiss(position: Int)

    fun onItemAdd(movie: Movie, position: Int)

    fun onItemSwipedToRight(movie: Movie, position: Int)
}