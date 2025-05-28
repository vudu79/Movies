package ru.vodolatskii.movies.presentation.utils.contentRV

interface ContentItemTouchHelperListener {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDismiss(position: Int)
}