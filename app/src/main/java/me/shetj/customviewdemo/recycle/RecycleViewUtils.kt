package me.shetj.customviewdemo.recycle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import me.shetj.base.ktx.dp2px
import me.shetj.custom.layoutManager.BaseItemTouchCallback
import me.shetj.custom.layoutManager.ItemTouchListener
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.page.MeetingPageLayoutManager
import me.shetj.customviewdemo.page.MeetingPageLayoutManager.HORIZONTAL
import me.shetj.customviewdemo.page.PagerSnapHelper
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
        val testAdapter =
            TestAdapter(IntArray(30).map { (it + Random.nextInt(3)).toString() }.toMutableList())
        val itemTouchHelperSwipe =
            ItemTouchHelper(object : BaseItemTouchCallback(object : ItemTouchListener {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val removeItem = swipeAdapter.data.removeAt(viewHolder.bindingAdapterPosition)
                    swipeAdapter.notifyItemRemoved(viewHolder.bindingAdapterPosition)
                    swipeAdapter.addData(swipeAdapter.itemCount, removeItem)
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
                            ) / 2)
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
            itemAnimator = DefaultItemAnimator()
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
        it.findViewById<RecyclerView>(R.id.recycle_grid).apply {
            adapter = testAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                private val mHeight = 5f.dp2px() //分割线高度

                /*
                 * etItemOffsets：通过Rect为每个Item设置偏移，用于绘制Decoration。
                 * onDraw：通过该方法，在Canvas上绘制内容，在绘制Item之前调用。
                 * （如果没有通过getItemOffsets设置偏移的话，Item的内容会将其覆盖）
                 * onDrawOver：通过该方法，在Canvas上绘制内容,在Item之后调用。
                 * (画的内容会覆盖在item的上层)
                 */

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val position = parent.getChildAdapterPosition(view)
                    when (position % 4) {
                        0 -> {
                            outRect.bottom = mHeight
                            outRect.right = mHeight
                        }
                        1 -> {
                            outRect.left = mHeight
                            outRect.bottom = mHeight
                        }
                        2 ->{
                            outRect.top = mHeight
                            outRect.right = mHeight
                        }
                        3->{
                            outRect.top = mHeight
                            outRect.left = mHeight
                        }
                    }
                }

                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    super.onDraw(c, parent, state)
                }

                override fun onDrawOver(
                    c: Canvas,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.onDrawOver(c, parent, state)
                }
            })
            layoutManager = MeetingPageLayoutManager(2, 2, HORIZONTAL)
            PagerSnapHelper().attachToRecyclerView(this)
        }
    }
}