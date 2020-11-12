package me.shetj.customviewdemo.recycle

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import me.shetj.custom.layoutManager.BaseLayoutManager
import kotlin.math.min


class SwipeLayoutManager(
    private val recyclerView: RecyclerView,
    private val itemTouchHelper: ItemTouchHelper
) : BaseLayoutManager() {

    protected val MAX_COUNT: Int
        get() = min(4, itemCount)
    protected val DEFAULT_SCALE: Float
        get() = 0.1f
    protected val DEFAULT_TRANSLATE_Y: Float
        get() = 50f


    override fun calculateChildrenSite(recycler: RecyclerView.Recycler) {
        for (i in (0 until MAX_COUNT).reversed()) {
            addView(recycler, i)
        }
    }

    private fun addView(recycler: RecyclerView.Recycler, position: Int): View {
        return recycler.getViewForPosition(position).also {
            addView(it)
            measureChildWithMargins(it, 0, 0)
            layoutView(recycler,it, position)
        }
    }

    //居中显示view
    @SuppressLint("ClickableViewAccessibility")
    private fun layoutView(
        recycler: RecyclerView.Recycler,
        view: View,
        position: Int
    ) {
        //获取view的宽高
        val width = getDecoratedMeasuredWidth(view)
        val height = getDecoratedMeasuredHeight(view)
        //获取空余的宽高
        val widthSpace = getWidth() - width
        val heightSpace = getHeight() - height
        //布局
        layoutDecorated(
            view,
            widthSpace / 2,
            heightSpace / 2,
            width + widthSpace / 2,
            height + heightSpace / 2
        )
        //根据position 缩小平移
        when {
            position == MAX_COUNT -> {
                view.scaleX = 1 - (position - 1) * DEFAULT_SCALE
                view.scaleY = 1 - (position - 1) * DEFAULT_SCALE
                view.translationY = (position - 1) * DEFAULT_TRANSLATE_Y
            }
            position > 0 -> {
                val scale = 1 - position * DEFAULT_SCALE
                view.scaleX = scale
                view.scaleY = scale
                view.translationY = position * DEFAULT_TRANSLATE_Y
            }
            else -> {
                view.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val childViewHolder = recyclerView.getChildViewHolder(view)
                        //只让第一个滑动
                        if (childViewHolder.bindingAdapterPosition == 0) {
                            itemTouchHelper.startSwipe(childViewHolder)
                        }
                    }
                    return@setOnTouchListener true
                }
            }
        }
    }
}