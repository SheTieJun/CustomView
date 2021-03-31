package me.shetj.choice.adapter.node

import android.view.View
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.choice.R
import me.shetj.choice.adapter.node.NodeAdapter.Companion.EXPAND_COLLAPSE_PAYLOAD
import me.shetj.choice.model.node.Semester

class FirstProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 1
    override val layoutId: Int
        get() = R.layout.item_node_first

    override fun convert(helper: BaseViewHolder, data: BaseNode) {
        val (title) = data as Semester
        helper.setText(R.id.tv_name, title)
        setArrowSpin(helper, data, false)
    }

    override fun convert(helper: BaseViewHolder, item: BaseNode, payloads: List<Any>) {
        super.convert(helper, item, payloads)

    }

    private fun setArrowSpin(helper: BaseViewHolder, data: BaseNode, isAnimate: Boolean) {
        val (title, childNode) = data as Semester

    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        // 这里使用payload进行增量刷新（避免整个item刷新导致的闪烁，不自然）
        getAdapter()!!.expandOrCollapse(
            position,
            true,
            true,
            EXPAND_COLLAPSE_PAYLOAD
        )
    }
}