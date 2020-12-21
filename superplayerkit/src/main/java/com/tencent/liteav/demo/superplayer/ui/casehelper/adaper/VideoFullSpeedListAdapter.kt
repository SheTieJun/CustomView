package com.tencent.liteav.demo.superplayer.ui.casehelper.adaper

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tencent.liteav.demo.superplayer.R


/**
 * Project Name:LiZhiWeiKe
 * Package Name:com.lizhiweike.lecture.adapter
 * Created by tom on 2018/2/1 18:25 .
 *
 *
 * Copyright (c) 2016—2017 https://www.lizhiweike.com all rights reserved.
 */
class VideoFullSpeedListAdapter(data: ArrayList<Float>) : BaseQuickAdapter<Float, BaseViewHolder>(R.layout.superplayer_item_video_full_speed, data) {
    private var curSpeed = 0f

    fun setCurSpeed(curSpeed: Float) {
        this.curSpeed = curSpeed
    }

    override fun convert(holder: BaseViewHolder, item: Float) {
        item.let {
            holder.setText(R.id.content,  item.toString())
            holder.setTextColor(R.id.content, if (curSpeed == item) getColor(context,R.color.orange) else getColor(context,R.color.white))
            holder.getView<TextView>(R.id.content)?.apply {
                typeface = if (curSpeed == item) {
                    Typeface.defaultFromStyle(Typeface.BOLD);
                }else {
                    Typeface.defaultFromStyle(Typeface.NORMAL);
                }
            }
        }
    }

}