package ru.vodolatskii.movies.presentation.utils.contentRV

import ru.vodolatskii.movies.data.models.Doc

interface ContentItemTouchHelperListener {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDismiss(position: Int)

    fun onItemAdd(doc: Doc, position: Int)
}