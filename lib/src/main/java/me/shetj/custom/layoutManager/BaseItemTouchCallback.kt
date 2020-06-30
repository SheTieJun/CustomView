package me.shetj.custom.layoutManager

import android.graphics.Canvas
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min


open class BaseItemTouchCallback(private val touchListener: ItemTouchListener?) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return touchListener?.getMovementFlags(recyclerView, viewHolder)?: makeMovementFlags(0, 0)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return touchListener?.isItemViewSwipeEnabled()?:false
    }

    override fun isLongPressDragEnabled(): Boolean {
        return touchListener?.isLongPressDragEnabled()?:false
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return touchListener?.onMove(recyclerView, viewHolder, target)?:false
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
        touchListener?.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        touchListener?.onSwiped(viewHolder,direction)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.post { //必须post 否则部分属性无效
            viewHolder.itemView.rotation = 0f
            viewHolder.itemView.animate().alpha(1f).scaleX(1.0f).scaleY(1.0f).apply {
                duration = 100
                interpolator = AccelerateInterpolator()
            }.start()
        }
    }

}