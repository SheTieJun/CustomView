package com.tencent.liteav.superplayer.casehelper.adaper

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tencent.liteav.superplayer.R
import com.tencent.liteav.superplayer.model.entity.VideoQuality

class QualityAdapter(data:ArrayList<VideoQuality>) :
        BaseQuickAdapter<VideoQuality, BaseViewHolder>(R.layout.superplayer_quality_item_view,data) {

    private var mClickPos: Int = -1

    override fun convert(holder: BaseViewHolder, item: VideoQuality) {
            holder.getView<TextView>(R.id.superplayer_tv_quality).apply {
                isSelected = mClickPos == holder.adapterPosition
            }.text = item.title
        }

    /**
     * 设置默认选中的清晰度
     *
     * @param position
     */
    fun setDefaultSelectedQuality(position: Int) {
        var position = position
        if (position < 0) position = 0
        mClickPos = position
        notifyDataSetChanged()
    }


}