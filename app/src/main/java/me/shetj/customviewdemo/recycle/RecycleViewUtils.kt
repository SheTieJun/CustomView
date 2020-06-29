package me.shetj.customviewdemo.recycle

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import me.shetj.custom.layoutManager.BaseItemTouchCallback
import me.shetj.custom.layoutManager.ItemTouchListener
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.utils.createDialog
import java.util.*
import kotlin.random.Random


fun showDialogRecycle(context: Context) {
    createDialog(context, R.layout.layout_recycle_swip) {
        val swipeAdapter = SwipeAdapter(IntArray(20).map { (it + Random.nextInt(3)).toString() }.toMutableList())
        val moveAdapter = SwipeAdapter(IntArray(30).map { (it+ Random.nextInt(3)).toString() }.toMutableList())
        val itemTouchHelperSwipe = ItemTouchHelper(BaseItemTouchCallback(object : ItemTouchListener {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                swipeAdapter.remove(viewHolder.adapterPosition)
                swipeAdapter.notifyDataSetChanged()
            }
        }))

        val itemTouchHelperMove = ItemTouchHelper(BaseItemTouchCallback(object : ItemTouchListener {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                targetViewHolder: RecyclerView.ViewHolder
            ): Boolean {
                val adapterPosition = viewHolder.adapterPosition
                val targetPosition = targetViewHolder.adapterPosition
                Collections.swap(moveAdapter.data,adapterPosition, targetPosition)
                moveAdapter.notifyItemMoved(adapterPosition, targetPosition)
                return true
            }
        }))

        //卡片滑动
        it.findViewById<RecyclerView>(R.id.recycle_swipe).apply {
            adapter = swipeAdapter.apply {
                setAnimationWithDefault( BaseQuickAdapter.AnimationType.AlphaIn)
            }
            layoutManager = SwipeLayoutManager(this,itemTouchHelperSwipe)
            itemTouchHelperSwipe.attachToRecyclerView(this)
        }
        //拖拽交换位置
        it.findViewById<RecyclerView>(R.id.recycle_move).apply {
            adapter = moveAdapter
            layoutManager = GridLayoutManager(context,3)
            itemTouchHelperMove.attachToRecyclerView(this)
        }
    }
}