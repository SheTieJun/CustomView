package me.shetj.custom.layoutManager

import android.graphics.Canvas
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min


interface ItemTouchListener {

    val DEF_SCALE: Float
        get() = 0.9f
    val DEF_ALPHA: Float
        get() = 0.8f

    val DEF_ROTATION: Float
        get() = 25f

    fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        //拖动
        val dragFlags =
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        //侧滑,0 表示不作为
        val swipeFlags =
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        targetViewHolder: RecyclerView.ViewHolder
    ): Boolean = false


    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    fun isItemViewSwipeEnabled(): Boolean = false
    fun isLongPressDragEnabled(): Boolean = true
    fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_SWIPE -> {
                viewHolder.itemView.apply {
                    val alpha =
                        min(1 - abs(dX) / width.toFloat(), 1 - abs(dY / 2) / height.toFloat())
                    rotation = (dX / width.toFloat()) * DEF_ROTATION
                    setAlpha(alpha)
                    translationX = dX;
                }
            }
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                viewHolder.itemView.apply {
                    viewHolder.itemView.animate().alpha(DEF_ALPHA).scaleX(DEF_SCALE)
                        .scaleY(DEF_SCALE).apply {
                        duration = 100
                        interpolator = AccelerateInterpolator()
                    }.start()
                }
            }
        }
    }

}