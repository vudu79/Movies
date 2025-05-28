package ru.vodolatskii.movies.presentation.utils.contentRV

import android.R.attr
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class ContentItemTouchHelperCallback(private var listener: ContentItemTouchHelperListener, context: Context) : ItemTouchHelper.SimpleCallback(0, (ItemTouchHelper.RIGHT + ItemTouchHelper.LEFT)) {
//    private val deleteIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete)
//    private val editIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_edit)
//    private val intrinsicWidth = deleteIcon?.intrinsicWidth ?: 0
//    private val intrinsicHeight = deleteIcon?.intrinsicHeight ?: 0
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
    private val cont = context
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (source.itemViewType != target.itemViewType) {
            return false
        }
        return listener.onItemMove(source.adapterPosition, target.adapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Toast.makeText(cont, "dfsdf", Toast.LENGTH_SHORT).show()
        listener.onItemDismiss(viewHolder.adapterPosition)

        when (attr.direction) {
            ItemTouchHelper.END -> {
                Toast.makeText(cont, "end", Toast.LENGTH_SHORT).show()

            }
            ItemTouchHelper.START -> {
                Toast.makeText(cont, "start", Toast.LENGTH_SHORT).show()

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
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        if (dX < 0) { // Swiping to the left (delete)
            background.color = backgroundColorDelete
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            background.draw(c)
            val textX = itemView.right + dX / 2
            val textY = itemView.top + itemHeight / 2 + 10
            c.drawText("Удалить", textX.toFloat(), textY.toFloat(), textPaint)
        } else { // Swiping to the right (edit)
            background.color = backgroundColorToFavorite
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
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