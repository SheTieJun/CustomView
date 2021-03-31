package me.shetj.choice.adapter

import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.base.BaseSAdapter
import me.shetj.choice.R
import me.shetj.choice.model.Group
import me.shetj.choice.model.Semester


class GroupAdapter(private val semester: Semester) : BaseSAdapter<Group, BaseViewHolder>(R.layout.item_group_view) {

    init {
        setNewInstance(semester.list)
    }

    override fun convert(holder: BaseViewHolder, item: Group) {
        holder.setText(R.id.tv_name, item.title)
        val mAdapter = StudentAdapter(item)
        holder.getView<RecyclerView>(R.id.recycleView_student).apply {
            isVisible = item.isExpand
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        holder.getView<CheckBox>(R.id.check_box).apply {
            isChecked = item.isSelect
            setOnCheckedChangeListener { _, isChecked ->
                checkSemesterSelect(isChecked)
                mAdapter.selectAll(isChecked)
            }
        }
    }

    private fun checkSemesterSelect(checked: Boolean) {
        if (!semester.isSelect && checked){
            semester.isSelect = checked
        }
    }

    override fun convert(holder: BaseViewHolder, item: Group, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        holder.getView<CheckBox>(R.id.check_box).apply {
            isChecked = item.isSelect
        }
        holder.getView<RecyclerView>(R.id.recycleView_student).apply {
            isVisible = item.isExpand
        }
    }

    fun selectAll(isChecked: Boolean){
        semester.isSelect = isChecked
        semester.list.forEachIndexed { index, group ->
            group.isSelect = isChecked
            notifyItemChanged(index,isChecked)
        }
    }

    /**
     * 隐藏/展开
     */
    fun changeShowItem(position: Int) {
        getItem(position).apply {
            isExpand =!isExpand
            notifyItemChanged(position,isExpand)
        }
    }

}