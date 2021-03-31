package me.shetj.choice.model.node

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode


data class Semester(
    val title: String,
    override val childNode: MutableList<BaseNode>?
) : BaseExpandNode() {

    init {
        isExpanded = false
    }

    //是否选择全部
    var isSelect: Boolean = false


    companion object {
        fun mock(): MutableList<BaseNode> {

            return mutableListOf<BaseNode>().apply {
                repeat(5) {
                    add(Semester(childNode = Group.mock(), title = "$it:学期"))
                }
            }
        }
    }
}