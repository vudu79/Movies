package ru.vodolatskii.movies.presentation.utils.contentRV

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class ContentItemTouchHelperCallback(
    private val recyclerView: RecyclerView,
) : ItemTouchHelper.Callback() {
    private val adapter = recyclerView.adapter as ContentAdapter
    private val background = ColorDrawable()
    private val backgroundColorDelete = Color.parseColor("#f44336")
    private val backgroundColorToFavorite = Color.parseColor("#2196F3")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (source.itemViewType != target.itemViewType) {
            return false
        }
        return adapter.onItemMove(source.adapterPosition, target.adapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            16 -> {
                val position = viewHolder.adapterPosition
                val removedDoc = adapter.getData()[viewHolder.adapterPosition]
                adapter.onItemDismiss(position)
                Snackbar.make(recyclerView, "Удалено ${removedDoc.name} ", Snackbar.LENGTH_LONG)
                    .setAction(
                        "Вернуть?"
                    ) {
                        adapter.onItemAdd(removedDoc, position)
                    }.show()
            }

            32 -> {
                val position = viewHolder.adapterPosition
                val favoriteMovie = adapter.getData()[viewHolder.adapterPosition]
                adapter.onItemSwipedToRight(favoriteMovie, position)  // заменить
                Snackbar.make(
                    recyclerView,
                    "В избранном ${favoriteMovie.name} ",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(
                        "Убрать",
                        View.OnClickListener {
                            adapter.onItemAdd(favoriteMovie, position)
                        })
                    .show()
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        if (dX < 0) {
            background.color = backgroundColorDelete
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)
            val textX = itemView.right + dX / 2
            val textY = itemView.top + itemHeight / 2 + 10
            c.drawText("Удалить", textX.toFloat(), textY.toFloat(), textPaint)
        } else {
            background.color = backgroundColorToFavorite
            background.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
            )
            background.draw(c)
            val textX = itemView.left + dX / 2
            val textY = itemView.top + itemHeight / 2 + 10
            c.drawText("В избранное", textX.toFloat(), textY.toFloat(), textPaint)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}