package me.shetj.customviewdemo.country

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.customviewdemo.R

class PhoneAreaCodeAdapter(list: MutableList<AreaCodeModel>?) :
    BaseQuickAdapter<AreaCodeModel, BaseViewHolder>(
        R.layout.item_area_code, list
    ) {

    private var english = false

    fun setEnglish(english: Boolean) {
        this.english = english
        notifyDataSetChanged()
    }


    override fun convert(holder: BaseViewHolder, item: AreaCodeModel) {
        if (english) {
            holder.setText(R.id.position, item.en)
        } else {
            holder.setText(R.id.position, item.name)
        }
        holder.setText(R.id.code, "+" + item.tel)
    }
}