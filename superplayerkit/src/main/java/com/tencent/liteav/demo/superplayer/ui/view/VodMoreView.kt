package com.tencent.liteav.demo.superplayer.ui.view

import android.content.*
import android.media.AudioManager
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.tencent.liteav.demo.superplayer.*
import com.tencent.liteav.demo.superplayer.ui.casehelper.VideoCaseHelper

/**
 * Created by yuejiaoli on 2018/7/4.
 *
 * 更多选项弹框
 *
 * 1、声音调节seekBar回调[.mVolumeChangeListener]
 *
 * 2、亮度调节seekBar回调[.mLightChangeListener]
 *
 * 3、倍速选择回调[.onCheckedChanged]
 *
 * 4、镜像、硬件加速开关回调[.onCheckedChanged]
 */
class VodMoreView : RelativeLayout {
    private var mContext: Context? = null
    private var mCallback // 回调
            : Callback? = null
    private var mAudioManager // 音频管理器
            : AudioManager? = null
    private val config: SuperPlayerGlobalConfig = SuperPlayerGlobalConfig.instance
    private var mCaseHelper: VideoCaseHelper? =null

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
        LayoutInflater.from(mContext).inflate(R.layout.superplayer_control_case_view, this)
        mCaseHelper = VideoCaseHelper(this)
        val config: SuperPlayerGlobalConfig = SuperPlayerGlobalConfig.instance
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mCaseHelper?.setCurSpeed(config.speed)
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
     * 倍速选择监听
     *
     * @param radioGroup
     * @param checkedId
     */
      fun onCheckedChanged(speed:Float) {
        if (mCallback != null) {
            mCallback!!.onSpeedChange(speed)
        }
        config.speed = speed
    }


    fun onDestroyTimeCallBack(){
        mCaseHelper?.onDestroy()
    }

    fun updateSpeedChange(speedLevel: Float) {
        mCaseHelper?.setCurSpeed(speedLevel)
    }

    /**
     * 回调
     */
    interface Callback {
        /**
         * 播放速度更新回调
         *
         * @param speedLevel
         */
        fun onSpeedChange(speedLevel: Float)

    }
}