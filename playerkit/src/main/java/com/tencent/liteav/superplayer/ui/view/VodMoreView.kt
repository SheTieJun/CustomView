package com.tencent.liteav.superplayer.ui.view

import android.content.*
import android.util.AttributeSet
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import com.tencent.liteav.superplayer.*
import com.tencent.liteav.superplayer.casehelper.VideoCaseHelper


class VodMoreView : RelativeLayout, CompoundButton.OnCheckedChangeListener {
    private var mContext: Context? = null
    private var mCallback // 回调
            : Callback? = null
    private val config: SuperPlayerGlobalConfig = SuperPlayerGlobalConfig.instance
    private var mCaseHelper: VideoCaseHelper? = null
    private var mSwitchMirror // 镜像开关
            : Switch? = null
    private var mSwitchAccelerate // 硬解开关
            : Switch? = null
    private var mLayoutaccelerate // 倍速按钮所在布局
            : View? = null
    private var mLayoutMirror // 镜像按钮所在布局
            : View? = null
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
        mCaseHelper?.setCurSpeed(config.getSpeedVale())
        mSwitchMirror = findViewById<View>(R.id.superplayer_switch_mirror) as Switch
        mSwitchAccelerate = findViewById<View>(R.id.superplayer_switch_accelerate) as Switch
        mSwitchAccelerate!!.isChecked = config.enableHWAcceleration
        mSwitchMirror!!.setOnCheckedChangeListener(this)
        mSwitchAccelerate!!.setOnCheckedChangeListener(this)
        mLayoutaccelerate = findViewById(R.id.superplayer_ll_enable_accelerate)
        mLayoutMirror = findViewById(R.id.superplayer_ll_mirror)
        mLayoutaccelerate?.isVisible = config.isShowAccelerate
        mLayoutMirror?.isVisible = config.isShowMirror
        findViewById<View>(R.id.ll_time_style).isVisible = config.isShowTimeStyle
        findViewById<View>(R.id.iRecyclerView_case_time).isVisible = config.isShowTimeStyle

        findViewById<View>(R.id.tv_pla_style).isVisible = config.isShowPlayStyle
        findViewById<View>(R.id.iRecyclerView_case_play_mode).isVisible = config.isShowPlayStyle
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
    fun onCheckedChanged(speed: Float) {
        if (mCallback != null) {
            mCallback!!.onSpeedChange(speed)
        }
        config.speed = speed
    }


    fun getCaseHelper(): VideoCaseHelper? {
        return mCaseHelper
    }

    fun onDestroyTimeCallBack() {
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

        /**
         * 镜像开关回调
         *
         * @param isMirror
         */
        fun onMirrorChange(isMirror: Boolean)

        /**
         * 硬解开关回调
         *
         * @param isAccelerate
         */
        fun onHWAcceleration(isAccelerate: Boolean)
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, isChecked: Boolean) {
        if (compoundButton.id == R.id.superplayer_switch_mirror) {
            if (mCallback != null) {
                mCallback!!.onMirrorChange(isChecked)
            }
        } else if (compoundButton.id == R.id.superplayer_switch_accelerate) {
            val config: SuperPlayerGlobalConfig = SuperPlayerGlobalConfig.instance
            config.enableHWAcceleration = !config.enableHWAcceleration
            mSwitchAccelerate!!.isChecked = config.enableHWAcceleration
            if (mCallback != null) {
                mCallback!!.onHWAcceleration(config.enableHWAcceleration)
            }
        }
    }
}