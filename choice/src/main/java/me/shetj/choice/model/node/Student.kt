package me.shetj.choice.model.node

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode


data class Student(val title: String,
                   override val childNode: MutableList<BaseNode>? = null
): BaseExpandNode() {
    init {
        isExpanded = false
    }
    companion object{
        fun mock():MutableList<BaseNode> {
            return mutableListOf<BaseNode>().apply {
                repeat(5){
                    add(Student("学生:$it",childNode = null))
                }
            }
        }
    }
}