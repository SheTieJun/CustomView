package me.shetj.choice.adapter

import android.util.Log
import android.widget.CheckBox
import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.base.BaseSAdapter
import me.shetj.base.ktx.isEmpty
import me.shetj.choice.R
import me.shetj.choice.model.Group
import me.shetj.choice.model.Student


class StudentAdapter(private val isEdit: Boolean = true) :
    BaseSAdapter<Student, BaseViewHolder>(R.layout.item_student_view) {
    var group:Group ?= null
        private set
    val studentLiveDate:MutableLiveData<Boolean> = MutableLiveData()

    fun setData(group : Group){
        this.group = group
        setNewInstance(group.list)
    }

    init {

        setOnItemClickListener { adapter, view, position ->
            getItem(position).apply {
                isSelect = !isSelect
                notifyItemChanged(position,isSelect)
                checkGroupSelect(isSelect)
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: Student) {
        holder.setGone(R.id.check_box_student, !isEdit)
        holder.setText(R.id.tv_name, item.title)
        holder.getView<CheckBox>(R.id.check_box_student).apply {
            isChecked = item.isSelect
            setOnCheckedChangeListener { buttonView, isChecked ->
                if(!buttonView.isPressed) {
                    //如果不是通过按钮，就不触发真正的监听
                    return@setOnCheckedChangeListener
                }
                Log.i("student", holder.adapterPosition.toString())
                item.isSelect = isChecked
                checkGroupSelect(isChecked)
            }
        }
    }

    private fun checkGroupSelect(checked: Boolean) {
        Log.i("student", checked.toString())
        if (!group!!.isSelect && checked){
            studentLiveDate.value = (true)
            return
        }
        if (group!!.isSelect && !checked){
            if ( data.find { it.isSelect }.isEmpty()){
                studentLiveDate.value = (false)
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: Student, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        Log.i("student0", holder.adapterPosition.toString())
        holder.getView<CheckBox>(R.id.check_box_student).apply {
            isChecked = item.isSelect
        }
    }

    /**
     * 选中全部
     */
    fun selectAll(isChecked: Boolean) {
        group!!.isSelect = isChecked
        group!!.list.forEachIndexed { index, student ->
            student.isSelect = isChecked
            notifyItemChanged(index,isChecked)
        }
    }

}