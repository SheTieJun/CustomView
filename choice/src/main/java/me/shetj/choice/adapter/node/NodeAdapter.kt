package me.shetj.choice.adapter.node

import android.view.View
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.choice.model.node.Group
import me.shetj.choice.model.node.Semester
import me.shetj.choice.model.node.Student




class NodeAdapter : BaseNodeAdapter() {
    init {
        // 普通的item provider
        addNodeProvider(ThirdProvider())
        addNodeProvider(FirstProvider())
        addNodeProvider(SecondProvider())

    }

    companion object {
        val EXPAND_COLLAPSE_PAYLOAD = 110
    }


    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        val node: BaseNode? = data[position]
        if (node is Semester) {
            return 1
        } else if (node is Group) {
            return 2
        } else if (node is Student) {
            return 3
        }
        return -1
    }


}