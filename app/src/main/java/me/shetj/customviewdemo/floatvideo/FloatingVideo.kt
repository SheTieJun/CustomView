package me.shetj.customviewdemo.floatvideo

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.R

class FloatingVideo : StandardGSYVideoPlayer {
    constructor(context: Context?, fullFlag: Boolean?) : super(
        context,
        fullFlag
    ) {
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    }

    override fun init(context: Context) {
        if (activityContext != null) {
            mContext = activityContext
        } else {
            mContext = context
        }
        initInflate(mContext)
        mTextureViewContainer = findViewById<View>(R.id.surface_container) as ViewGroup
        mStartButton = findViewById(R.id.start)
        if (isInEditMode) return
        mScreenWidth = activityContext.resources.displayMetrics.widthPixels
        mScreenHeight = activityContext.resources.displayMetrics.heightPixels
        mAudioManager = activityContext.applicationContext
            .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        findViewById<View>(R.id.iv_close).setOnClickListener { destroyFloat(false) }
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_floating_video
    }

    override fun startPrepare() {
        if (gsyVideoManager.listener() != null) {
            gsyVideoManager.listener().onCompletion()
        }
        gsyVideoManager.setListener(this)
        gsyVideoManager.playTag = mPlayTag
        gsyVideoManager.playPosition = mPlayPosition
        mAudioManager.requestAudioFocus(
            onAudioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )
        //((Activity) getActivityContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBackUpPlayingBufferState = -1
        gsyVideoManager.prepare(mUrl, mMapHeadData, mLooping, mSpeed, mCache, mCachePath, null)
        setStateAndUi(GSYVideoView.CURRENT_STATE_PREPAREING)
    }

    override fun onAutoCompletion() {
        setStateAndUi(GSYVideoView.CURRENT_STATE_AUTO_COMPLETE)
        mSaveChangeViewTIme = 0
        if (mTextureViewContainer.childCount > 0) {
            mTextureViewContainer.removeAllViews()
        }
        if (!mIfCurrentIsFullscreen) gsyVideoManager.setLastListener(null)
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener)
        //((Activity) getActivityContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        releaseNetWorkState()
        if (mVideoAllCallBack != null && isCurrentMediaListener) {
            Debuger.printfLog("onAutoComplete")
            mVideoAllCallBack.onAutoComplete(mOriginUrl, mTitle, this)
        }
    }

    override fun onCompletion() {
        //make me normal first
        setStateAndUi(GSYVideoView.CURRENT_STATE_NORMAL)
        mSaveChangeViewTIme = 0
        if (mTextureViewContainer.childCount > 0) {
            mTextureViewContainer.removeAllViews()
        }
        if (!mIfCurrentIsFullscreen) {
            gsyVideoManager.setListener(null)
            gsyVideoManager.setLastListener(null)
        }
        gsyVideoManager.currentVideoHeight = 0
        gsyVideoManager.currentVideoWidth = 0
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener)
        //((Activity) getActivityContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        releaseNetWorkState()
        destroyFloat(true)
    }

    override fun onVideoSizeChanged() {
        super.onVideoSizeChanged()
        val mVideoWidth = gsyVideoManager.currentVideoWidth
        val mVideoHeight = gsyVideoManager.currentVideoHeight
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (mVideoWidth > mVideoHeight) {
                val fl = ArmsUtils.dp2px(169f).toFloat() / mVideoWidth.toFloat()
                updateWH((mVideoWidth * fl).toInt(), (mVideoHeight * fl).toInt())
            } else {
                val fl = ArmsUtils.dp2px(93f).toFloat() / mVideoHeight.toFloat()
                updateWH((mVideoWidth * fl).toInt(), (mVideoHeight * fl).toInt())
            }
        }
    }

    override fun getActivityContext(): Context {
        return context
    }

    override fun isShowNetConfirm(): Boolean {
        return false
    }

    override fun showWifiDialog() {
        //悬浮窗不考虑提醒
        startPlayLogic()
    }
}