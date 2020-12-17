package com.tencent.liteav.demo.superplayer.ui.view

import android.content.*
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.tencent.liteav.demo.superplayer.*

/**
 * 滑动手势设置音量、亮度时显示的提示view
 */
class VolumeBrightnessProgressLayout : RelativeLayout {
    private var mImageCenter // 中心图片：亮度提示、音量提示
            : ImageView? = null
    private var mProgressBar // 进度条
            : ProgressBar? = null
    private var mHideRunnable // 隐藏view的runnable
            : HideRunnable? = null
    private var mDuration = 1000 // view消失延迟时间(秒)

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context)
            .inflate(R.layout.superplayer_video_volume_brightness_progress_layout, this)
        mImageCenter = findViewById<View>(R.id.superplayer_iv_center) as ImageView
        mProgressBar = findViewById<View>(R.id.superplayer_pb_progress_bar) as ProgressBar
        mHideRunnable = HideRunnable()
        visibility = GONE
    }

    /**
     * 显示
     */
    fun show() {
        visibility = VISIBLE
        removeCallbacks(mHideRunnable)
        postDelayed(mHideRunnable, mDuration.toLong())
    }

    /**
     * 设置progressBar的进度值
     *
     * @param progress
     */
    fun setProgress(progress: Int) {
        mProgressBar!!.progress = progress
    }

    /**
     * 设置view消失的延迟时间
     *
     * @param duration
     */
    fun setDuration(duration: Int) {
        mDuration = duration
    }

    /**
     * 设置显示的图片，亮度提示图片或者音量提示图片
     *
     * @param resource
     */
    fun setImageResource(resource: Int) {
        mImageCenter!!.setImageResource(resource)
    }

    /**
     * 隐藏view的runnable
     */
    private inner class HideRunnable : Runnable {
        override fun run() {
            this@VolumeBrightnessProgressLayout.visibility = GONE
        }
    }
}