package com.tencent.liteav.superplayer.casehelper.adaper

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tencent.liteav.superplayer.R

/**
 * Project Name:LiZhiWeiKe
 * Package Name:com.lizhiweike.lecture.adapter
 * Created by tom on 2018/2/1 18:25 .
 *
 *
 * Copyright (c) 2016—2017 https://www.lizhiweike.com all rights reserved.
 */
class VideoFullPlayModeListAdapter(data: ArrayList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.superplayer_item_video_full_speed, data) {
    private var curSpeed = "单课播放"

    fun setCurPlayMode(curSpeed: String) {
        this.curSpeed = curSpeed
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.content, item)
        holder.setTextColor(
            R.id.content,
            if (curSpeed == item) getColor(context, R.color.orange) else getColor(
                context,
                R.color.superplayer_white
            )
        )
        holder.getView<TextView>(R.id.content)?.apply {
            typeface = if (curSpeed == item) {
                Typeface.defaultFromStyle(Typeface.BOLD);
            } else {
                Typeface.defaultFromStyle(Typeface.NORMAL);
            }
        }
    }

}