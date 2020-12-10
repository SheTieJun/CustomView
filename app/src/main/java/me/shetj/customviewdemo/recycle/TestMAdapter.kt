package me.shetj.customviewdemo.recycle

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.base.BaseSAdapter
import me.shetj.customviewdemo.R


class TestMAdapter(dates: MutableList<String>) :
    BaseSAdapter<String, BaseViewHolder>(R.layout.item_test_card, dates) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setImageResource(
            R.id.iv_image,
            if (item.toInt() % 2 == 0) R.drawable.p_1 else R.drawable.p_2
        )
        holder.setText(R.id.position,""+item)
    }
}