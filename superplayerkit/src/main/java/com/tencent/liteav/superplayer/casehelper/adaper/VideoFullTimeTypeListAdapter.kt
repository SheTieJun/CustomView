package com.tencent.liteav.superplayer.casehelper.adaper

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tencent.liteav.superplayer.R
import com.tencent.liteav.superplayer.timer.TimeType

/**
 * Project Name:LiZhiWeiKe
 * Package Name:com.lizhiweike.lecture.adapter
 * Created by tom on 2018/2/1 18:25 .
 *
 *
 * Copyright (c) 2016—2017 https://www.lizhiweike.com all rights reserved.
 */
class VideoFullTimeTypeListAdapter(data: ArrayList<TimeType>) : BaseQuickAdapter<TimeType, BaseViewHolder>(R.layout.superplayer_item_full_time_type, data) {
    private var position = -1
    fun setPosition(targetPos: Int) { //如果不相等，说明有变化
        if (position != targetPos) {
            val old = position
            position = targetPos
            // -1 表示默认不做任何变化
            if (old != -1) {
                notifyItemChanged(old + headerLayoutCount)
            }
            if (targetPos != -1) {
                notifyItemChanged(targetPos + headerLayoutCount)
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: TimeType) {
        holder.setText(R.id.name, item.name)
        holder.setTextColor(R.id.name, if (position == holder.adapterPosition - headerLayoutCount) getColor(context,R.color.orange) else getColor(context,R.color.superplayer_white))
        holder.getView<TextView>(R.id.name).apply {
            typeface = if (position == holder.adapterPosition - headerLayoutCount) {
                Typeface.defaultFromStyle(Typeface.BOLD);
            }else {
                Typeface.defaultFromStyle(Typeface.NORMAL);
            }
        }
    }
}