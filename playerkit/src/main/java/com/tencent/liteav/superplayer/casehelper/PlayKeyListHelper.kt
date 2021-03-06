package com.tencent.liteav.superplayer.casehelper

import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tencent.liteav.superplayer.R
import com.tencent.liteav.superplayer.ui.player.FullScreenPlayer


typealias onNext = (Any) ->Unit

class PlayKeyListHelper(private val fullScreenPlayer: FullScreenPlayer) {
    private val mLlSpeedList = fullScreenPlayer.findViewById<View>(R.id.ll_data_list)
    private val mTvKey: TextView = fullScreenPlayer.findViewById(R.id.superplayer_tv_play_list)
    private val mTvName: TextView = fullScreenPlayer.findViewById(R.id.tv_name)
    private val mRecycleView: RecyclerView = fullScreenPlayer.findViewById(R.id.iRecyclerView_key)
    private val mIvNext: ImageView = fullScreenPlayer.findViewById(R.id.iv_next)
    private var mAdapter: BaseQuickAdapter<*, *>? = null
    private var position: Int = 0
    private var onNext: onNext? =null
    init {

        mRecycleView.apply {
            layoutManager = LinearLayoutManager(fullScreenPlayer.context)
        }
        mLlSpeedList.setOnClickListener {
            showLectureList(false)
        }
        mTvKey.setOnClickListener {
            showLectureList(true)
        }
        mTvName.setOnClickListener { showLectureList(true) }
        mIvNext.setOnClickListener {
            nextOne()
        }
    }

    fun nextOne() {
        if ((mRecycleView.adapter as? BaseQuickAdapter<*, *>)?.data?.size?:0 > position + 1) {
            (mRecycleView.adapter as? BaseQuickAdapter<*, *>)?.getItem(position + 1)?.apply {
                onNext?.invoke(this)
                updatePosition(position + 1)
            }
        }
    }

    fun setKeyAndAdapter(name: String?, adapter: BaseQuickAdapter<*, *>?, position: Int = 0,onNext: onNext?) {
        mTvName.text = name
        mTvKey.text = name
        mRecycleView.adapter = adapter
        adapter?.onAttachedToRecyclerView(mRecycleView)
        this.mAdapter = adapter
        this.onNext = onNext
        mTvKey.isVisible = adapter != null
        if (position != 0){
            this.position = position
        }
        updatePosition(this.position)
    }

    fun updatePosition(position: Int) {
        this.position = position
        mIvNext.isVisible = position < mAdapter?.itemCount ?: 0
    }

    private fun showLectureList(isShow: Boolean) {
        if (isShow) {
            fullScreenPlayer.hide()
            mLlSpeedList?.visibility = View.VISIBLE
            mLlSpeedList?.animation = AnimationUtils.loadAnimation(fullScreenPlayer.context, R.anim.slide_right_in)
            mRecycleView.smoothToPosition(position)
        } else {
            mLlSpeedList?.visibility = View.GONE
            mLlSpeedList?.animation = AnimationUtils.loadAnimation(fullScreenPlayer.context, R.anim.slide_right_exit)
        }
    }

    fun RecyclerView.smoothToPosition(position: Int) {
        smoothToPosition(
                position + 1,
                 object : LinearSmoothScroller(context) {
                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                        return displayMetrics?.let {
                            100f / displayMetrics.densityDpi
                        } ?: super.calculateSpeedPerPixel(displayMetrics)
                    }
                })

    }

    fun RecyclerView?.smoothToPosition(position: Int, scroller: LinearSmoothScroller?) {
        this?.let {
            scroller?.let {
                it.targetPosition = position
                layoutManager?.startSmoothScroll(scroller)
            } ?: this.smoothScrollToPosition(position)

        }
    }

}