package me.shetj.choice.adapter

import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.base.BaseSAdapter
import me.shetj.choice.R
import me.shetj.choice.model.Semester


class SemesterAdapter() : BaseSAdapter<Semester, BaseViewHolder>(R.layout.item_semester_view) {


    override fun convert(holder: BaseViewHolder, item: Semester) {
        holder.setText(R.id.tv_name, item.title)
        holder.getView<RecyclerView>(R.id.recycleViewGroup).apply {
            val mAdapter: GroupAdapter = if (adapter == null) {
                GroupAdapter().apply {
                    setOnItemClickListener { _, _, position ->
                        this.changeShowItem(position)
                    }
                }.also { mAdapter ->
                    mAdapter.groupLiveDate.observeForever {
                        if (it != null) {
                            mAdapter.semester?.isSelect = it
                            notifyItemChanged(this@SemesterAdapter.data.indexOf(mAdapter.semester), false)
                        }
                    }
                    adapter = mAdapter
                }
            } else {
                (adapter as GroupAdapter)
            }
            mAdapter.setData(item)
            isVisible = item.isExpand
            layoutManager = LinearLayoutManager(context)
            holder.getView<CheckBox>(R.id.check_box_semester).apply {
                isChecked = item.isSelect
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if(!buttonView.isPressed) {
                        //如果不是通过按钮，就不触发真正的监听
                        return@setOnCheckedChangeListener
                    }
                    mAdapter.selectAll(isChecked)
                }
            }
        }
    }


    override fun convert(holder: BaseViewHolder, item: Semester, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        holder.getView<CheckBox>(R.id.check_box_semester).apply {
            isChecked = item.isSelect
        }
        holder.getView<RecyclerView>(R.id.recycleViewGroup).apply {
            isVisible = item.isExpand
        }
    }


    /**
     * 隐藏/展开
     */
    fun changeShowItem(position: Int) {
        getItem(position).apply {
            isExpand = !isExpand
            notifyItemChanged(position, isExpand)
        }
    }
}