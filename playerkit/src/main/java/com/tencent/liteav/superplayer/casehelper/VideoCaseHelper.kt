package com.tencent.liteav.superplayer.casehelper

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tencent.liteav.superplayer.GlobalConfig
import com.tencent.liteav.superplayer.R
import com.tencent.liteav.superplayer.SuperPlayerGlobalConfig
import com.tencent.liteav.superplayer.timer.TimeType.Companion.getPlayModeList
import com.tencent.liteav.superplayer.timer.TimeType.Companion.getTimeTypeList2
import com.tencent.liteav.superplayer.timer.TimerConfigure
import com.tencent.liteav.superplayer.timer.TimerConfigure.Companion.REPEAT_MODE_ALL
import com.tencent.liteav.superplayer.timer.TimerConfigure.Companion.REPEAT_MODE_ONE
import com.tencent.liteav.superplayer.casehelper.adaper.VideoFullPlayModeListAdapter
import com.tencent.liteav.superplayer.casehelper.adaper.VideoFullSpeedListAdapter
import com.tencent.liteav.superplayer.casehelper.adaper.VideoFullTimeTypeListAdapter
import com.tencent.liteav.superplayer.ui.view.VodMoreView
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

/**
 * 视频的[SuperPlayerView]的菜单按钮操作
 * 1.倍数切换
 * 2.定时功能
 * 3.循环功能
 */

class VideoCaseHelper(private val mVodMoreView: VodMoreView): TimerConfigure.CallBack{

    private var adapter: VideoFullSpeedListAdapter? =null
    private var context :Context   = mVodMoreView.context
    private var iRecyclerViewSpeed :  RecyclerView? =null
    private var iRecyclerViewTime :  RecyclerView? =null
    private var iRecyclerViewPlayMode :  RecyclerView? =null
    private var content:View ?= null
    private var timeShow :TextView ?= null
    private var playMode = if (!TimerConfigure.instance.isRepeatOne()) "顺序播放" else "单曲循环"
    private var mAdapter = VideoFullPlayModeListAdapter(getPlayModeList())
    init {
        TimerConfigure.instance.addCallBack(this)
        initView(mVodMoreView)
    }

    private fun initView(rootView: ViewGroup?) {
        rootView?.apply {
            content = findViewById(R.id.ll_case_list)
            timeShow = findViewById(R.id.time_show)
            iRecyclerViewSpeed = findViewById(R.id.iRecyclerView_case_speed)
            iRecyclerViewTime = findViewById(R.id.iRecyclerView_case_time)
            iRecyclerViewPlayMode = findViewById(R.id.iRecyclerView_case_play_mode)
            initSpeed()
            initTime()
            initPlayMode()
            content?.setOnClickListener {
                showCase(false)
            }
        }
    }

    private fun showCase(isShow: Boolean) {
        if (isShow) {
            content?.visibility = View.VISIBLE
            content?.animation = AnimationUtils.loadAnimation(context, R.anim.slide_right_in)
        } else {
            content?.visibility = View.GONE
            content?.animation = AnimationUtils.loadAnimation(context, R.anim.slide_right_exit)
        }
    }

    fun setCurSpeed(speedRate: Float) {
        adapter?.setCurSpeed(speedRate)
        adapter?.notifyDataSetChanged()
    }


    private fun initPlayMode() {
        mAdapter.setCurPlayMode(playMode)
        iRecyclerViewPlayMode?.layoutManager =  LinearLayoutManager(context,  LinearLayoutManager.HORIZONTAL, false)
        mAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
            mAdapter.getItem(position).let {
                if (playMode != it) {
                    mAdapter.setCurPlayMode(it)
                    TimerConfigure.instance.changePlayMode(context)
                }
            }
        }
        iRecyclerViewPlayMode?.adapter = mAdapter
    }

    private fun initTime() {
        val mAdapter = VideoFullTimeTypeListAdapter(getTimeTypeList2())
        mAdapter.setPosition(TimerConfigure.instance.getTimeTypePosition())
        iRecyclerViewTime?.layoutManager =  GridLayoutManager(context, 4)
        mAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
            mAdapter.setPosition(position)
            TimerConfigure.instance.setTimeType(mAdapter.getItem(position), context, position)
        }
        iRecyclerViewTime?.adapter = mAdapter
    }


    private fun initSpeed() {
        val speedList = arrayListOf(1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
        adapter = VideoFullSpeedListAdapter(speedList)
        adapter?.setCurSpeed(SuperPlayerGlobalConfig.instance.speed)
        iRecyclerViewSpeed?.layoutManager = GridLayoutManager(context, 5)
        adapter?.setOnItemClickListener { adapter1, _, position ->
            val speedRate = adapter1.getItem(position) as Float
            if (speedRate != SuperPlayerGlobalConfig.instance.getSpeedVale()) {
                SuperPlayerGlobalConfig.instance.speed = speedRate
                mVodMoreView.onCheckedChanged(speedRate)
                setCurSpeed(speedRate)
            }
        }
        iRecyclerViewSpeed?.adapter = adapter
    }

    //region 定时关闭
    override fun onTick(progress: Long) {
        var pattern = "mm:ss"
        if (progress >= 1000 * 60 * 60) {
            pattern = "HH:mm:ss"
        }
        val date = Date(progress)
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        timeShow?.text = String.format("%s后关闭", format.format(date))
    }

    override fun onStateChange(state: Int) {
        when (state) {
            TimerConfigure.STATE_COMPLETE -> {
                timeShow?.text = ""
            }
            TimerConfigure.STATE_CLOSE -> {
                timeShow?.text = ""
            }
            TimerConfigure.STATE_COURSE -> {
            }
            else -> {
            }
        }
    }


    override fun onChangeModel(repeatMode: Int) {
        playMode = when (repeatMode) {
             REPEAT_MODE_ONE -> {
                "单课循环"
            }
             REPEAT_MODE_ALL -> {
                "顺序播放"
            }
            else -> "顺序播放"
        }
        mAdapter.setCurPlayMode(playMode)
    }
    //endregion定时关闭

    fun onDestroy() {
        TimerConfigure.instance.removeCallBack(this)
    }
}