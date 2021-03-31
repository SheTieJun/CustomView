package me.shetj.choice.adapter.node

import android.view.View
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.choice.R
import me.shetj.choice.model.node.Group

class SecondProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 2
    override val layoutId: Int
        get() = R.layout.item_node_second

    override fun convert(helper: BaseViewHolder, data: BaseNode) {
        val entity = data as Group
        helper.setText(R.id.tv_name, entity.title)
        if (entity.isExpanded) {
        } else {
        }
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        val entity = data as Group
        if (entity.isExpanded) {
            getAdapter()!!.collapse(position)
        } else {
            getAdapter()!!.expandAndCollapseOther(position,isCollapseChild = false)
        }
    }
}