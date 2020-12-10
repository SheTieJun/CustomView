package me.shetj.customviewdemo.recycle

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.base.BaseSAdapter
import me.shetj.customviewdemo.R


class TestAdapter(dates: MutableList<MultiItem>) :
    BaseMultiItemQuickAdapter<MultiItem, BaseViewHolder>( dates) {
    
    init {
        
        addItemType(2,R.layout.item_test_card)
        addItemType(1,R.layout.item_swipe_card)
    }
    
    override fun convert(holder: BaseViewHolder, item: MultiItem) {
         
    }

}