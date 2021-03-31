package me.shetj.choice.adapter

import android.widget.CheckBox
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.base.BaseSAdapter
import me.shetj.choice.R
import me.shetj.choice.model.Group
import me.shetj.choice.model.Student


class StudentAdapter(private val group: Group,private val isEdit: Boolean = false) :
    BaseSAdapter<Student, BaseViewHolder>(R.layout.item_student_view) {

    init {
        setNewInstance(group.list)
        setOnItemClickListener { adapter, view, position ->
            getItem(position).apply {
                isSelect = !isSelect
                notifyItemChanged(position,isSelect)
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: Student) {
        holder.setGone(R.id.check_box, !isEdit)
        holder.setText(R.id.tv_name, item.title)
        holder.getView<CheckBox>(R.id.check_box).apply {
            isChecked = item.isSelect
            setOnCheckedChangeListener { buttonView, isChecked ->
                checkGroupSelect(isChecked)
                item.isSelect = isChecked
            }
        }
    }

    private fun checkGroupSelect(checked: Boolean) {
        if (!group.isSelect && checked){
            group.isSelect = checked
        }
    }

    override fun convert(holder: BaseViewHolder, item: Student, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        holder.getView<CheckBox>(R.id.check_box).apply {
            isChecked = payloads[0] as Boolean
        }
    }

    /**
     * 选中全部
     */
    fun selectAll(isChecked: Boolean) {
        group.isSelect = isChecked
        group.list.forEachIndexed { index, student ->
            student.isSelect = isChecked
            notifyItemChanged(index,isChecked)
        }
    }

}