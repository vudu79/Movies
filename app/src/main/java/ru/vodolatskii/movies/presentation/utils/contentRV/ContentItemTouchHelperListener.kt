package ru.vodolatskii.movies.presentation.utils.contentRV

import ru.vodolatskii.movies.data.entity.Movie

interface ContentItemTouchHelperListener {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDismiss(position: Int)

    fun onItemAdd(movie: Movie, position: Int)
}