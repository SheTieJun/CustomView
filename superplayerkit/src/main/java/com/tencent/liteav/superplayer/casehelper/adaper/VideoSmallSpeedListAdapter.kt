package com.tencent.liteav.superplayer.casehelper.adaper

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tencent.liteav.superplayer.R

class VideoSmallSpeedListAdapter(data: ArrayList<Float>) : BaseQuickAdapter<Float, BaseViewHolder>(R.layout.superplayer_item_video_full_speed_2, data) {
    private var curSpeed = 0f
      override fun convert(holder: BaseViewHolder, item: Float) {
          holder.setText(R.id.content,  item.toString()+"倍")
          holder.setTextColor(R.id.content, if (curSpeed == item) ContextCompat.getColor(context, R.color.orange) else ContextCompat.getColor(context, R.color.superplayer_white))
          holder.getView<TextView>(R.id.content).apply {
              typeface = if (curSpeed == item) {
                  Typeface.defaultFromStyle(Typeface.BOLD);
              }else {
                  Typeface.defaultFromStyle(Typeface.NORMAL);
              }
          }
    }

    fun setCurSpeed(curSpeed: Float) {
        this.curSpeed = curSpeed
    }
}