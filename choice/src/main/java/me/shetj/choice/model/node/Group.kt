package me.shetj.choice.model.node

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode


class Group(val title: String,
            override val childNode: MutableList<BaseNode>?
) : BaseExpandNode(){
    init {
        isExpanded = false
    }
    var isSelect : Boolean = false
    companion object{
        fun mock():MutableList<BaseNode> {

            return mutableListOf<BaseNode>().apply {
                repeat(5){
                    add(Group(childNode = Student.mock(),title = "$it:班级"))
                }
            }
        }
    }

}