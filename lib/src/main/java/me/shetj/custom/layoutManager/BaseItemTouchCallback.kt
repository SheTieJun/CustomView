package me.shetj.custom.layoutManager

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min


class BaseItemTouchCallback(private val touchListener: ItemTouchListener) : ItemTouchHelper.Callback() {

    protected val DEF_SCALE :Float
        get() = 0.9f
    protected val DEF_ALPHA :Float
        get() = 0.8f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return touchListener.getMovementFlags(recyclerView, viewHolder)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return touchListener.isItemViewSwipeEnabled()
    }

    override fun isLongPressDragEnabled(): Boolean {
        return touchListener.isLongPressDragEnabled()
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return touchListener.onMove(recyclerView, viewHolder, target)
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_SWIPE -> {
                viewHolder.itemView.apply {
                    val alpha = min( 1 - abs(dX) / width.toFloat(),1 - abs(dY/2) / height.toFloat())
                    rotation = ( dX / width.toFloat()) * 15
                    setAlpha(alpha)
                    translationX = dX;
                }
            }
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                viewHolder.itemView.apply {
                    viewHolder.itemView.animate().alpha(DEF_ALPHA).scaleX(DEF_SCALE).scaleY(DEF_SCALE).apply {
                        duration = 200
                    }.start()
                }
            }
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        touchListener.onSwiped(viewHolder,direction)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        viewHolder.itemView.post { //必须post 否则部分属性无效
            viewHolder.itemView.rotation = 0f
            viewHolder.itemView.animate().alpha(1f).scaleX(1.0f).scaleY(1.0f).apply {
                duration = 200
            }.start()
        }
    }

}