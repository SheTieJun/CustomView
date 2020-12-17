package com.tencent.liteav.demo.superplayer.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.tencent.liteav.demo.superplayer.R
import com.tencent.liteav.demo.superplayer.model.entity.VideoQuality
import java.util.*

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
            : ListView? = null
    private var mAdapter // 画质列表适配器
            : QualityAdapter? = null
    private var mList // 画质列表
            : MutableList<VideoQuality?>? = null
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
        mList = ArrayList()
        LayoutInflater.from(mContext).inflate(R.layout.superplayer_quality_popup_view, this)
        mListView = findViewById<View>(R.id.superplayer_lv_quality) as ListView
        mListView!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if (mCallback != null) {
                    if (mList != null && mList!!.size > 0) {
                        val quality = mList!!.get(position)
                        if (quality != null && position != mClickPos) {
                            mCallback!!.onQualitySelect(quality)
                        }
                    }
                }
                mClickPos = position
                mAdapter!!.notifyDataSetChanged()
            }
        mAdapter = QualityAdapter()
        mListView!!.adapter = mAdapter
    }

    /**
     * 设置回调
     *
     * @param callback
     */
    fun setCallback(callback: Callback?) {
        mCallback = callback
    }

    /**
     * 设置画质列表
     *
     * @param list
     */
    fun setVideoQualityList(list: List<VideoQuality?>?) {
        mList!!.clear()
        mList!!.addAll(list!!)
        if (mAdapter != null) {
            mAdapter!!.notifyDataSetChanged()
        }
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
        mAdapter!!.notifyDataSetChanged()
    }

    internal inner class QualityAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return mList!!.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            var convertView: View? = convertView
            if (convertView == null) {
                convertView = QualityItemView(mContext)
            }
            val itemView = convertView as QualityItemView
            itemView.isSelected = false
            val quality = mList!![position]
            itemView.setQualityName(quality!!.title)
            if (mClickPos == position) {
                itemView.isSelected = true
            }
            return itemView
        }
    }

    /**
     * 画质item view
     */
    internal inner class QualityItemView : RelativeLayout {
        private var mTvQuality: TextView? = null

        constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
            context,
            attrs,
            defStyle
        ) {
            init(context)
        }

        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
            init(context)
        }

        constructor(context: Context?) : super(context) {
            init(context)
        }

        private fun init(context: Context?) {
            LayoutInflater.from(context).inflate(R.layout.superplayer_quality_item_view, this)
            mTvQuality = findViewById<View>(R.id.superplayer_tv_quality) as TextView
        }

        /**
         * 设置画质名称
         *
         * @param qualityName
         */
        fun setQualityName(qualityName: String?) {
            mTvQuality!!.text = qualityName
        }

        /**
         * 设置画质item是否为选择状态
         *
         * @param isChecked
         */
        override fun setSelected(isChecked: Boolean) {
            mTvQuality!!.isSelected = isChecked
        }
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