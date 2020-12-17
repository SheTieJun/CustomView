package com.tencent.liteav.demo.superplayer.ui.view

import android.app.Activity
import android.content.*
import android.media.AudioManager
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.tencent.liteav.demo.superplayer.*
import com.tencent.liteav.demo.superplayer.SuperPlayerDef.PlayerType

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
class VodMoreView : RelativeLayout, RadioGroup.OnCheckedChangeListener,
    CompoundButton.OnCheckedChangeListener {
    private var mContext: Context? = null
    private var mSeekBarVolume // 音量seekBar
            : SeekBar? = null
    private var mSeekBarLight // 亮度seekBar
            : SeekBar? = null
    private var mSwitchMirror // 镜像开关
            : Switch? = null
    private var mSwitchAccelerate // 硬解开关
            : Switch? = null
    private var mCallback // 回调
            : Callback? = null
    private var mAudioManager // 音频管理器
            : AudioManager? = null
    private var mRadioGroup // 倍速选择radioGroup
            : RadioGroup? = null
    private var mRbSpeed1 // 1.0倍速按钮
            : RadioButton? = null
    private var mRbSpeed125 // 1.25倍速按钮
            : RadioButton? = null
    private var mRbSpeed15 // 1.5倍速按钮
            : RadioButton? = null
    private var mRbSpeed2 // 2.0倍速按钮
            : RadioButton? = null
    private var mLayoutSpeed // 倍速按钮所在布局
            : LinearLayout? = null
    private var mLayoutMirror // 镜像按钮所在布局
            : LinearLayout? = null
    private var mVolumeBroadcastReceiver: VolumeBroadcastReceiver? = null

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
        LayoutInflater.from(mContext).inflate(R.layout.superplayer_more_popup_view, this)
        mLayoutSpeed = findViewById<View>(R.id.superplayer_ll_speed) as LinearLayout
        mRadioGroup = findViewById<View>(R.id.superplayer_rg) as RadioGroup
        mRbSpeed1 = findViewById<View>(R.id.superplayer_rb_speed1) as RadioButton
        mRbSpeed125 = findViewById<View>(R.id.superplayer_rb_speed125) as RadioButton
        mRbSpeed15 = findViewById<View>(R.id.superplayer_rb_speed15) as RadioButton
        mRbSpeed2 = findViewById<View>(R.id.superplayer_rb_speed2) as RadioButton
        mRadioGroup!!.setOnCheckedChangeListener(this)
        mSeekBarVolume = findViewById<View>(R.id.superplayer_sb_audio) as SeekBar
        mSeekBarLight = findViewById<View>(R.id.superplayer_sb_light) as SeekBar
        mLayoutMirror = findViewById<View>(R.id.superplayer_ll_mirror) as LinearLayout
        mSwitchMirror = findViewById<View>(R.id.superplayer_switch_mirror) as Switch
        mSwitchAccelerate = findViewById<View>(R.id.superplayer_switch_accelerate) as Switch
        val config: SuperPlayerGlobalConfig = SuperPlayerGlobalConfig.instance
        mSwitchAccelerate!!.isChecked = config.enableHWAcceleration
        mSeekBarVolume!!.setOnSeekBarChangeListener(mVolumeChangeListener)
        mSeekBarLight!!.setOnSeekBarChangeListener(mLightChangeListener)
        mSwitchMirror!!.setOnCheckedChangeListener(this)
        mSwitchAccelerate!!.setOnCheckedChangeListener(this)
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        updateCurrentVolume()
        updateCurrentLight()
    }

    private fun updateCurrentVolume() {
        val curVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val percentage = curVolume.toFloat() / maxVolume
        val progress = (percentage * mSeekBarVolume!!.max).toInt()
        mSeekBarVolume!!.progress = progress
    }

    private fun updateCurrentLight() {
        val activity = mContext as Activity?
        val window = activity!!.window
        val params = window.attributes
        params.screenBrightness = getActivityBrightness(mContext as Activity?)
        window.attributes = params
        if (params.screenBrightness == -1f) {
            mSeekBarLight!!.progress = 100
            return
        }
        mSeekBarLight!!.progress = (params.screenBrightness * 100).toInt()
    }

    private val mVolumeChangeListener: SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateVolumeProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        }

    private fun updateVolumeProgress(progress: Int) {
        val percentage = progress.toFloat() / mSeekBarVolume!!.max
        if (percentage < 0 || percentage > 1) return
        if (mAudioManager != null) {
            val maxVolume = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val newVolume = (percentage * maxVolume).toInt()
            mAudioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
        }
    }

    private val mLightChangeListener: SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateBrightProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        }

    private fun updateBrightProgress(progress: Int) {
        val activity = mContext as Activity?
        val window = activity!!.window
        val params = window.attributes
        params.screenBrightness = progress * 1.0f / 100
        if (params.screenBrightness > 1.0f) {
            params.screenBrightness = 1.0f
        }
        if (params.screenBrightness <= 0.01f) {
            params.screenBrightness = 0.01f
        }
        window.attributes = params
        mSeekBarLight!!.progress = progress
    }

    /**
     * 镜像、硬解开关监听
     *
     * @param compoundButton
     * @param isChecked
     */
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
    override fun onCheckedChanged(radioGroup: RadioGroup, checkedId: Int) {
        if (checkedId == R.id.superplayer_rb_speed1) {
            mRbSpeed1!!.isChecked = true
            if (mCallback != null) {
                mCallback!!.onSpeedChange(1.0f)
            }
        } else if (checkedId == R.id.superplayer_rb_speed125) {
            mRbSpeed125!!.isChecked = true
            if (mCallback != null) {
                mCallback!!.onSpeedChange(1.25f)
            }
        } else if (checkedId == R.id.superplayer_rb_speed15) {
            mRbSpeed15!!.isChecked = true
            if (mCallback != null) {
                mCallback!!.onSpeedChange(1.5f)
            }
        } else if (checkedId == R.id.superplayer_rb_speed2) {
            mRbSpeed2!!.isChecked = true
            if (mCallback != null) {
                mCallback!!.onSpeedChange(2.0f)
            }
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            updateCurrentVolume()
            updateCurrentLight()
            registerReceiver()
        } else {
            unregisterReceiver()
        }
    }

    fun setBrightProgress(progress: Int) {
        updateBrightProgress(progress)
    }

    /**
     * 更新播放视频类型
     *
     * @param playType
     */
    fun updatePlayType(playType: PlayerType) {
        if (playType == PlayerType.VOD) {
            mLayoutSpeed!!.visibility = VISIBLE
            mLayoutMirror!!.visibility = VISIBLE
        } else {
            mLayoutSpeed!!.visibility = GONE
            mLayoutMirror!!.visibility = GONE
        }
    }

    private inner class VolumeBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //媒体音量改变才通知
            if (VOLUME_CHANGED_ACTION == intent.action && intent.getIntExtra(
                    EXTRA_VOLUME_STREAM_TYPE,
                    -1
                ) == AudioManager.STREAM_MUSIC
            ) {
                updateCurrentVolume()
            }
        }
    }

    /**
     * 注册音量广播接收器
     * @return
     */
    fun registerReceiver() {
        mVolumeBroadcastReceiver = VolumeBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(VOLUME_CHANGED_ACTION)
        mContext!!.registerReceiver(mVolumeBroadcastReceiver, filter)
    }

    /**
     * 反注册音量广播监听器，需要与 registerReceiver 成对使用
     */
    fun unregisterReceiver() {
        try {
            mContext!!.unregisterReceiver(mVolumeBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    companion object {
        private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
        private const val EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE"

        /**
         * 获取当前亮度
         *
         * @param activity
         * @return
         */
        fun getActivityBrightness(activity: Activity?): Float {
            val localWindow = activity!!.window
            val params = localWindow.attributes
            return params.screenBrightness
        }
    }
}