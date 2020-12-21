package com.tencent.liteav.demo.superplayer.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.liteav.demo.superplayer.R
import com.tencent.liteav.demo.superplayer.model.entity.VideoQuality
import com.tencent.liteav.demo.superplayer.ui.casehelper.adaper.QualityAdapter
import com.tencent.liteav.demo.superplayer.ui.player.WindowPlayer
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by yuejiaoli on 2018/7/4.
 *
 * 视频画质选择弹框
 *
 * 1、设置画质列表[.setVideoQualityList]
 *
 * 2、设置默认选中的画质[.setDefaultSelectedQuality]
 */
class VodQualityView : RelativeLayout {
    private var mContext: Context? = null
    private var mCallback // 回调
            : Callback? = null
    private var mListView // 画质listView
            : RecyclerView? = null
    private var mAdapter // 画质列表适配器
            : QualityAdapter? = null
    private var mClickPos = -1 // 当前的画质下表

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        LayoutInflater.from(mContext).inflate(R.layout.superplayer_quality_popup_view, this)
        mListView = findViewById(R.id.superplayer_lv_quality)
        mAdapter = QualityAdapter(ArrayList()).apply {
            setOnItemClickListener { adapter, view, position ->
                if (mCallback != null) {
                        val quality = getItem(position)
                        if (position != mClickPos) {
                            mCallback!!.onQualitySelect(quality)
                        }
                }
                mClickPos = position
                mAdapter!!.notifyDataSetChanged()
            }
        }
        mListView!!.layoutManager = LinearLayoutManager(mContext)
        mListView!!.adapter = mAdapter
    }

    /**
     * 设置回调
     *
     * @param callback
     */
    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    /**
     * 设置画质列表
     *
     * @param ArrayList
     */
    fun setVideoQualityList(list: ArrayList<VideoQuality>?) {
        mAdapter?.setNewInstance(list)
    }

    /**
     * 设置默认选中的清晰度
     *
     * @param position
     */
    fun setDefaultSelectedQuality(position: Int) {
        mAdapter?.setDefaultSelectedQuality(position)
    }



    /**
     * 回调
     */
    interface Callback {
        /**
         * 画质选择回调
         *
         * @param quality
         */
        fun onQualitySelect(quality: VideoQuality)
    }
}