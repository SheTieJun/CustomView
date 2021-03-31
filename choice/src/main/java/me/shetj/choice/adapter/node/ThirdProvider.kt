package me.shetj.choice.adapter.node

import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.choice.R
import me.shetj.choice.model.node.Student

class ThirdProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 3
    override val layoutId: Int
        get() = R.layout.item_node_third

    override fun convert(helper: BaseViewHolder, data: BaseNode) {
        val (title) = data as Student
        helper.setText(R.id.tv_name, title)
    }
}