package me.shetj.customviewdemo.recycle

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import me.shetj.custom.layoutManager.BaseItemTouchCallback
import me.shetj.custom.layoutManager.ItemTouchListener
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.utils.createDialog
import java.util.*
import kotlin.math.sqrt
import kotlin.random.Random


fun showDialogRecycle(context: Context) {

    createDialog(context, R.layout.layout_recycle_swip) {
        val swipeAdapter =
            SwipeAdapter(IntArray(20).map { (it + Random.nextInt(2)).toString() }.toMutableList())
        val moveAdapter =
            SwipeAdapter(IntArray(30).map { (it + Random.nextInt(3)).toString() }.toMutableList())
        val itemTouchHelperSwipe =
            ItemTouchHelper(object : BaseItemTouchCallback(object : ItemTouchListener {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val removeItem = swipeAdapter.data.removeAt(viewHolder.adapterPosition)
                    swipeAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                    swipeAdapter.addData(swipeAdapter.itemCount,removeItem)
                }

                override fun isLongPressDragEnabled(): Boolean = false


            }) {
                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    when (actionState) {
                        ItemTouchHelper.ACTION_STATE_SWIPE -> {
                            val swipeValue = sqrt((dX * dX + dY * dY).toDouble())
                            var fraction = swipeValue / (sqrt(
                                (viewHolder.itemView.width * viewHolder.itemView.width
                                        + viewHolder.itemView.height * viewHolder.itemView.height).toDouble()
                            )/2)
                            // 边界修正 最大为1
                            if (fraction > 1) {
                                fraction = 1.0
                            }
                            val childCount = recyclerView.childCount
                            for (i in 0 until childCount) {
                                val child = recyclerView.getChildAt(i)
                                //第几层,举例子，count =7， 最后一个TopView（6）是第0层，
                                val level = childCount - i - 1
                                if (level > 0) {
                                    val scale = (1 - level * 0.1f + fraction * 0.1f).toFloat()
                                    child.scaleX = scale
                                    child.scaleY = scale
                                    child.translationY = ((level * 50f - fraction * 50f).toFloat())
                                }
                            }
                        }
                    }
                }
            })

        val itemTouchHelperMove = ItemTouchHelper(BaseItemTouchCallback(object : ItemTouchListener {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                targetViewHolder: RecyclerView.ViewHolder
            ): Boolean {
                val adapterPosition = viewHolder.bindingAdapterPosition
                val targetPosition = targetViewHolder.bindingAdapterPosition
                Collections.swap(moveAdapter.data, adapterPosition, targetPosition)
                moveAdapter.notifyItemMoved(adapterPosition, targetPosition)
                return true
            }
        }))

        //卡片滑动
        it.findViewById<RecyclerView>(R.id.recycle_swipe).apply {
            adapter = swipeAdapter.apply {
                setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn)
            }
            layoutManager = SwipeLayoutManager(this, itemTouchHelperSwipe)
            itemTouchHelperSwipe.attachToRecyclerView(this)
        }
        //拖拽交换位置
        it.findViewById<RecyclerView>(R.id.recycle_move).apply {
            adapter = moveAdapter
            layoutManager = GridLayoutManager(context, 3)
            itemTouchHelperMove.attachToRecyclerView(this)
        }
    }
}