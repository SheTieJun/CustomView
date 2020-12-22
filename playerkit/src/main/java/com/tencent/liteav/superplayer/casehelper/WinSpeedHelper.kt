package com.tencent.liteav.superplayer.casehelper

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencent.liteav.superplayer.GlobalConfig
import com.tencent.liteav.superplayer.R
import com.tencent.liteav.superplayer.SuperPlayerGlobalConfig
import com.tencent.liteav.superplayer.casehelper.adaper.VideoSmallSpeedListAdapter
import com.tencent.liteav.superplayer.ui.player.WindowPlayer


/**
 * 窗口倍数控制
 */
class WinSpeedHelper(private val windowPlayer: WindowPlayer) {
    private val mLlSpeedList = windowPlayer.findViewById<View>(R.id.ll_speed_list)
    private lateinit var speedAdapter: VideoSmallSpeedListAdapter
    private val mIvSpeed: ImageView = windowPlayer.findViewById(R.id.iv_speed)
    private val mRecycleView: RecyclerView = windowPlayer.findViewById(R.id.iRecyclerView)

    init {
        mRecycleView.apply {
            layoutManager = LinearLayoutManager(windowPlayer.context)
        }
        mLlSpeedList.setOnClickListener {
            showSpeedList(false)
        }
        mIvSpeed.setOnClickListener {
            showSpeedList(true)
        }
        initSpeed()
        showSpeedImage()
    }

    private fun initSpeed() {
        val speedList = arrayListOf(1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
        speedAdapter = VideoSmallSpeedListAdapter(speedList)
        speedAdapter.setCurSpeed(GlobalConfig.instance.getSpeedVale())
        val i = speedList.indexOf(GlobalConfig.instance.getSpeedVale())
        speedAdapter.setOnItemClickListener { adapter1, view, position ->
            val speedRate = adapter1.getItem(position) as Float
            if (speedRate != GlobalConfig.instance.getSpeedVale()) {
                GlobalConfig.instance.speed = speedRate
                windowPlayer.onSpeedChange(speedRate)
                speedAdapter.setCurSpeed(speedRate)
                speedAdapter.notifyDataSetChanged()
                showSpeedImage()
            }
            showSpeedList(false)
        }
        mRecycleView.adapter = speedAdapter
        mRecycleView.scrollToPosition(i)
    }


    private fun showSpeedList(isShow: Boolean) {
        if (isShow) {
            windowPlayer.hide()
            mLlSpeedList?.visibility = View.VISIBLE
            mLlSpeedList?.animation = AnimationUtils.loadAnimation(windowPlayer.context, R.anim.slide_right_in)
        } else {
            mLlSpeedList?.visibility = View.GONE
            mLlSpeedList?.animation = AnimationUtils.loadAnimation(windowPlayer.context, R.anim.slide_right_exit)
        }
    }

    fun showSpeedImage() {
        showSpeedImage(mIvSpeed)
    }

    companion object{
        fun showSpeedImage(mIvSpeed: ImageView) {
            when (GlobalConfig.instance.getSpeedVale()) {
                1.0f -> mIvSpeed.setImageResource(R.drawable.superplayer_1_0_speed)
                1.25f -> mIvSpeed.setImageResource(R.drawable.superplayer_1_25_speed)
                1.5f -> mIvSpeed.setImageResource(R.drawable.superplayer_1_5_speed)
                1.75f -> mIvSpeed.setImageResource(R.drawable.superplayer_1_75_speed)
                2.0f -> mIvSpeed.setImageResource(R.drawable.superplayer_2_0_speed)
            }
        }
    }
}