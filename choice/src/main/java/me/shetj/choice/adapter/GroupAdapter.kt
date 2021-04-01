package me.shetj.choice.adapter

import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.base.BaseSAdapter
import me.shetj.base.ktx.isEmpty
import me.shetj.choice.R
import me.shetj.choice.model.Group
import me.shetj.choice.model.Semester


/**
 *  notifyItemChanged(index, true)  true :表示下一级需要跟着这一级进行全选
 */
class GroupAdapter() : BaseSAdapter<Group, BaseViewHolder>(R.layout.item_group_view) {
    val groupLiveDate: MutableLiveData<Boolean> = MutableLiveData()
    var semester: Semester? = null
        private set
    fun setData(semester: Semester) {
        this.semester = semester
        setNewInstance(semester.list)
    }


    override fun convert(holder: BaseViewHolder, item: Group) {
        holder.setText(R.id.tv_name, item.title)
        holder.getView<RecyclerView>(R.id.recycleView_student).apply {
            isVisible = item.isExpand
            layoutManager = LinearLayoutManager(context)
            val mAdapter: StudentAdapter = if (adapter == null) {
                StudentAdapter().also { mAdapter ->
                    mAdapter.studentLiveDate.observeForever {
                        if (it != null) {
                            mAdapter.group?.isSelect = it
                            notifyItemChanged(data.indexOf(mAdapter.group), false)
                            post {
                                checkSemesterSelect(it)
                            }
                        }
                    }
                    adapter = mAdapter
                }
            } else {
                adapter as StudentAdapter
            }
            mAdapter.setData(item)
            holder.getView<CheckBox>(R.id.check_box).apply {
                isChecked = item.isSelect
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if(!buttonView.isPressed) {
                        //如果不是通过按钮，就不触发真正的监听
                        return@setOnCheckedChangeListener
                    }
                    mAdapter.selectAll(isChecked)
                    checkSemesterSelect(isChecked)
                }
            }
        }
    }

    private fun checkSemesterSelect(checked: Boolean) {
        if (!semester!!.isSelect && checked) {
            groupLiveDate.value = (true)
            return
        }
        if (semester!!.isSelect && !checked) {
            if (data.find { it.isSelect }.isEmpty()) {
                groupLiveDate.value = (false)
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: Group, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        holder.getView<RecyclerView>(R.id.recycleView_student).apply {
            isVisible = item.isExpand
            holder.getView<CheckBox>(R.id.check_box).apply {
                isChecked = item.isSelect
                if (payloads[0] as Boolean) {
                    (adapter as StudentAdapter).selectAll(item.isSelect)
                }
            }
        }
    }

    fun selectAll(isChecked: Boolean) {
        semester!!.isSelect = isChecked
        semester!!.list.forEachIndexed { index, group ->
            group.isSelect = isChecked
            group.list.forEach {
                it.isSelect = isChecked
            }
            notifyItemChanged(index, true)
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