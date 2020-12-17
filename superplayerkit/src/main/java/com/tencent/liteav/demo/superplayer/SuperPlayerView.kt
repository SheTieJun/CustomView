package com.tencent.liteav.demo.superplayer

import android.app.Activity
import android.app.AppOpsManager
import android.content.*
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.*
import com.tencent.liteav.basic.log.TXCLog
import com.tencent.liteav.demo.superplayer.SuperPlayerDef.*
import com.tencent.liteav.demo.superplayer.SuperPlayerModel.SuperPlayerURL
import com.tencent.liteav.demo.superplayer.model.SuperPlayer
import com.tencent.liteav.demo.superplayer.model.SuperPlayerImpl
import com.tencent.liteav.demo.superplayer.model.SuperPlayerObserver
import com.tencent.liteav.demo.superplayer.model.entity.PlayImageSpriteInfo
import com.tencent.liteav.demo.superplayer.model.entity.PlayKeyFrameDescInfo
import com.tencent.liteav.demo.superplayer.model.entity.VideoQuality
import com.tencent.liteav.demo.superplayer.model.net.LogReport
import com.tencent.liteav.demo.superplayer.model.utils.NetWatcher
import com.tencent.liteav.demo.superplayer.ui.case.VideoCaseHelper
import com.tencent.liteav.demo.superplayer.ui.player.*
import com.tencent.liteav.demo.superplayer.ui.view.DanmuView
import com.tencent.rtmp.TXLivePlayer
import com.tencent.rtmp.ui.TXCloudVideoView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 * 超级播放器view
 *
 * 具备播放器基本功能，此外还包括横竖屏切换、悬浮窗播放、画质切换、硬件加速、倍速播放、镜像播放、手势控制等功能，同时支持直播与点播
 * 使用方式极为简单，只需要在布局文件中引入并获取到该控件，通过[.playWithModel]传入[SuperPlayerModel]即可实现视频播放
 *
 * 1、播放视频[.playWithModel]
 * 2、设置回调[.setPlayerViewCallback]
 * 3、controller回调实现[.mControllerCallback]
 * 4、退出播放释放内存[.resetPlayer]
 */
class SuperPlayerView : RelativeLayout {
    private val OP_SYSTEM_ALERT_WINDOW = 24 // 支持TYPE_TOAST悬浮窗的最高API版本
    private var mContext: Context? = null
    private var mRootView // SuperPlayerView的根view
            : ViewGroup? = null
    private var mTXCloudVideoView // 腾讯云视频播放view
            : TXCloudVideoView? = null
    private var mFullScreenPlayer // 全屏模式控制view
            : FullScreenPlayer? = null
    private var mWindowPlayer // 窗口模式控制view
            : WindowPlayer? = null
    private var mFloatPlayer // 悬浮窗模式控制view
            : FloatPlayer? = null
    private var mDanmuView // 弹幕
            : DanmuView? = null
    private var mLayoutParamWindowMode // 窗口播放时SuperPlayerView的布局参数
            : ViewGroup.LayoutParams? = null
    private var mLayoutParamFullScreenMode // 全屏播放时SuperPlayerView的布局参数
            : ViewGroup.LayoutParams? = null
    private var mVodControllerWindowParams // 窗口controller的布局参数
            : LayoutParams? = null
    private var mVodControllerFullScreenParams // 全屏controller的布局参数
            : LayoutParams? = null
    private var mWindowManager // 悬浮窗窗口管理器
            : WindowManager? = null
    private var mWindowParams // 悬浮窗布局参数
            : WindowManager.LayoutParams? = null
    private var mPlayerViewCallback // SuperPlayerView回调
            : OnSuperPlayerViewCallback? = null
    private var mWatcher // 网络质量监视器
            : NetWatcher? = null
    private var mSuperPlayer: SuperPlayer? = null

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        mContext = context
        initView()
        initPlayer()
    }

    /**
     * 初始化view
     */
    private fun initView() {
        mRootView =
            LayoutInflater.from(mContext).inflate(R.layout.superplayer_vod_view, null) as ViewGroup
        mTXCloudVideoView =
            mRootView!!.findViewById<View>(R.id.superplayer_cloud_video_view) as TXCloudVideoView
        mFullScreenPlayer =
            mRootView!!.findViewById<View>(R.id.superplayer_controller_large) as FullScreenPlayer
        mWindowPlayer =
            mRootView!!.findViewById<View>(R.id.superplayer_controller_small) as WindowPlayer
        mFloatPlayer =
            mRootView!!.findViewById<View>(R.id.superplayer_controller_float) as FloatPlayer
        mDanmuView = mRootView!!.findViewById<View>(R.id.superplayer_danmuku_view) as DanmuView
        mVodControllerWindowParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mVodControllerFullScreenParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mFullScreenPlayer!!.setCallback(mControllerCallback)
        mWindowPlayer!!.setCallback(mControllerCallback)
        mFloatPlayer!!.setCallback(mControllerCallback)
        removeAllViews()
        mRootView!!.removeView(mDanmuView)
        mRootView!!.removeView(mTXCloudVideoView)
        mRootView!!.removeView(mWindowPlayer)
        mRootView!!.removeView(mFullScreenPlayer)
        mRootView!!.removeView(mFloatPlayer)
        addView(mTXCloudVideoView)
        addView(mDanmuView)
    }

    private fun initPlayer() {
        mSuperPlayer = SuperPlayerImpl(mContext, mTXCloudVideoView)
        mSuperPlayer!!.setObserver(mSuperPlayerObserver)
        if (mSuperPlayer!!.playerMode == PlayerMode.FULLSCREEN) {
            addView(mFullScreenPlayer)
            mFullScreenPlayer!!.hide()
        } else if (mSuperPlayer!!.playerMode == PlayerMode.WINDOW) {
            addView(mWindowPlayer)
            mWindowPlayer!!.hide()
        }
        post {
            if (mSuperPlayer!!.playerMode == PlayerMode.WINDOW) {
                mLayoutParamWindowMode = layoutParams
            }
            try {
                // 依据上层Parent的LayoutParam类型来实例化一个新的fullscreen模式下的LayoutParam
                val parentLayoutParamClazz: Class<*> = layoutParams.javaClass
                val constructor = parentLayoutParamClazz.getDeclaredConstructor(
                    Int::class.javaPrimitiveType, Int::class.javaPrimitiveType
                )
                mLayoutParamFullScreenMode = constructor.newInstance(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ) as ViewGroup.LayoutParams
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        LogReport.instance.setAppName(mContext)
        LogReport.Companion.instance.setPackageName(mContext)
        if (mWatcher == null) {
            mWatcher = NetWatcher(mContext)
        }
    }

    /**
     * 播放视频
     *
     * @param model
     */
    fun playWithModel(model: SuperPlayerModel) {
        if (model.videoId != null) {
            mSuperPlayer!!.play(model.appId, model.videoId!!.fileId, model.videoId!!.pSign)
        } else if (model.videoIdV2 != null) {
        } else if (model.multiURLs != null && !model.multiURLs!!.isEmpty()) {
            mSuperPlayer!!.play(model.appId, model.multiURLs, model.playDefaultIndex)
        } else {
            mSuperPlayer!!.play(model.url)
        }
    }

    /**
     * 开始播放
     *
     * @param url 视频地址
     */
    fun play(url: String?) {
        mSuperPlayer!!.play(url)
    }

    /**
     * 开始播放
     *
     * @param appId 腾讯云视频appId
     * @param url   直播播放地址
     */
    fun play(appId: Int, url: String?) {
        mSuperPlayer!!.play(appId, url)
    }

    /**
     * 开始播放
     *
     * @param appId  腾讯云视频appId
     * @param fileId 腾讯云视频fileId
     * @param psign  防盗链签名，开启防盗链的视频必填，非防盗链视频可不填
     */
    fun play(appId: Int, fileId: String?, psign: String?) {
        mSuperPlayer!!.play(appId, fileId, psign)
    }

    /**
     * 多分辨率播放
     *
     * @param appId           腾讯云视频appId
     * @param superPlayerURLS 不同分辨率数据
     * @param defaultIndex    默认播放Index
     */
    fun play(appId: Int, superPlayerURLS: List<SuperPlayerURL?>?, defaultIndex: Int) {
        mSuperPlayer!!.play(appId, superPlayerURLS, defaultIndex)
    }

    /**
     * 更新标题
     *
     * @param title 视频名称
     */
    private fun updateTitle(title: String?) {
        mWindowPlayer!!.updateTitle(title)
        mFullScreenPlayer!!.updateTitle(title)
    }

    /**
     * resume生命周期回调
     */
    fun onResume() {
        if (mDanmuView != null && mDanmuView!!.isPrepared && mDanmuView!!.isPaused) {
            mDanmuView!!.resume()
        }
        mSuperPlayer!!.resume()
    }

    /**
     * pause生命周期回调
     */
    fun onPause() {
        if (mDanmuView != null && mDanmuView!!.isPrepared) {
            mDanmuView!!.pause()
        }
        mSuperPlayer!!.pauseVod()
    }

    /**
     * 重置播放器
     */
    fun resetPlayer() {
        if (mDanmuView != null) {
            mDanmuView!!.release()
            mDanmuView = null
        }
        stopPlay()
    }

    /**
     * 停止播放
     */
    private fun stopPlay() {
        mSuperPlayer!!.stop()
        if (mWatcher != null) {
            mWatcher!!.stop()
        }
    }

    /**
     * 设置超级播放器的回掉
     *
     * @param callback
     */
    fun setPlayerViewCallback(callback: OnSuperPlayerViewCallback?) {
        mPlayerViewCallback = callback
    }

    /**
     * 控制是否全屏显示
     */
    private fun fullScreen(isFull: Boolean) {
        if (context is Activity) {
            val activity = context as Activity
            if (isFull) {
                //隐藏虚拟按键，并且全屏
                val decorView = activity.window.decorView ?: return
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                    decorView.systemUiVisibility = GONE
                } else if (Build.VERSION.SDK_INT >= 19) {
                    val uiOptions = (SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or SYSTEM_UI_FLAG_IMMERSIVE_STICKY or SYSTEM_UI_FLAG_FULLSCREEN)
                    decorView.systemUiVisibility = uiOptions
                }
            } else {
                val decorView = activity.window.decorView ?: return
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                    decorView.systemUiVisibility = VISIBLE
                } else if (Build.VERSION.SDK_INT >= 19) {
                    decorView.systemUiVisibility = SYSTEM_UI_FLAG_VISIBLE
                }
            }
        }
    }

    /**
     * 初始化controller回调
     */
    private val mControllerCallback: Player.Callback? = object : Player.Callback {
        override fun onSwitchPlayMode(playerMode: PlayerMode) {
//            if (playerMode == PlayerMode.FULLSCREEN) {
//                fullScreen(true)
//            } else {
//                fullScreen(false)
//            }
            mFullScreenPlayer!!.hide()
            mWindowPlayer!!.hide()
            mFloatPlayer!!.hide()
            //请求全屏模式
            if (playerMode == PlayerMode.FULLSCREEN) {
                if (mLayoutParamFullScreenMode == null) {
                    return
                }
                removeView(mWindowPlayer)
                addView(mFullScreenPlayer, mVodControllerFullScreenParams)
                TransitionManager.beginDelayedTransition(parent as ViewGroup)
                layoutParams = mLayoutParamFullScreenMode
                if (mSuperPlayer!!.getWidth() > mSuperPlayer!!.getHeight()) {
                    rotateScreenOrientation(Orientation.LANDSCAPE)
                }
//                layoutParams = mLayoutParamFullScreenMode
                if (mPlayerViewCallback != null) {
                    mPlayerViewCallback!!.onStartFullScreenPlay()
                }
            } else if (playerMode == PlayerMode.WINDOW) { // 请求窗口模式
                // 当前是悬浮窗
                if (mSuperPlayer!!.playerMode == PlayerMode.FLOAT) {
                    try {
                        val viewContext = context
                        var intent: Intent? = null
                        intent = if (viewContext is Activity) {
                            Intent(viewContext, viewContext.javaClass)
                        } else {
                            showToast(R.string.superplayer_float_play_fail)
                            return
                        }
                        mContext!!.startActivity(intent)
                        mSuperPlayer!!.pause()
                        if (mLayoutParamWindowMode == null) {
                            return
                        }
                        mWindowManager!!.removeView(mFloatPlayer)
                        mSuperPlayer!!.setPlayerView(mTXCloudVideoView)
                        mSuperPlayer!!.resume()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (mSuperPlayer!!.playerMode == PlayerMode.FULLSCREEN) { // 当前是全屏模式
                    if (mLayoutParamWindowMode == null) {
                        return
                    }
                    removeView(mFullScreenPlayer)
                    addView(mWindowPlayer, mVodControllerWindowParams)
                    TransitionManager.beginDelayedTransition(parent as ViewGroup)
                    layoutParams = mLayoutParamWindowMode
                    rotateScreenOrientation(Orientation.PORTRAIT)
                    if (mPlayerViewCallback != null) {
                        mPlayerViewCallback!!.onStopFullScreenPlay()
                    }
                }
            } else if (playerMode == PlayerMode.FLOAT) { //请求悬浮窗模式
                TXCLog.i(TAG, "requestPlayMode Float :" + Build.MANUFACTURER)
                val prefs: SuperPlayerGlobalConfig = SuperPlayerGlobalConfig.instance
                if (!prefs.enableFloatWindow) {
                    return
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0动态申请悬浮窗权限
                    if (!Settings.canDrawOverlays(mContext)) {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data = Uri.parse("package:" + mContext!!.packageName)
                        mContext!!.startActivity(intent)
                        return
                    }
                } else {
                    if (!checkOp(mContext, OP_SYSTEM_ALERT_WINDOW)) {
                        showToast(R.string.superplayer_enter_setting_fail)
                        return
                    }
                }
                mSuperPlayer!!.pause()
                mWindowManager =
                    mContext!!.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                mWindowParams = WindowManager.LayoutParams()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mWindowParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    mWindowParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
                }
                mWindowParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                mWindowParams!!.format = PixelFormat.TRANSLUCENT
                mWindowParams!!.gravity = Gravity.LEFT or Gravity.TOP
                val rect = prefs.floatViewRect
                mWindowParams!!.x = rect!!.x
                mWindowParams!!.y = rect.y
                mWindowParams!!.width = rect.width
                mWindowParams!!.height = rect.height
                try {
                    mWindowManager!!.addView(mFloatPlayer, mWindowParams)
                } catch (e: Exception) {
                    showToast(R.string.superplayer_float_play_fail)
                    return
                }
                val videoView = mFloatPlayer!!.floatVideoView
                if (videoView != null) {
                    mSuperPlayer!!.setPlayerView(videoView)
                    mSuperPlayer!!.resume()
                }
                // 悬浮窗上报
                LogReport.instance
                    .uploadLogs(LogReport.ELK_ACTION_FLOATMOE, 0, 0)
            }
            mSuperPlayer!!.switchPlayMode(playerMode)
        }

        override fun onBackPressed(playMode: PlayerMode?) {
            when (playMode) {
                PlayerMode.FULLSCREEN -> onSwitchPlayMode(PlayerMode.WINDOW)
                PlayerMode.WINDOW -> if (mPlayerViewCallback != null) {
                    mPlayerViewCallback!!.onClickSmallReturnBtn()
                }
                PlayerMode.FLOAT -> {
                    mWindowManager!!.removeView(mFloatPlayer)
                    if (mPlayerViewCallback != null) {
                        mPlayerViewCallback!!.onClickFloatCloseBtn()
                    }
                }
                else -> {
                }
            }
        }

        override fun onFloatPositionChange(x: Int, y: Int) {
            mWindowParams!!.x = x
            mWindowParams!!.y = y
            mWindowManager!!.updateViewLayout(mFloatPlayer, mWindowParams)
        }

        override fun onPause() {
            mSuperPlayer!!.pause()
            if (mSuperPlayer!!.playerType != PlayerType.VOD) {
                if (mWatcher != null) {
                    mWatcher!!.stop()
                }
            }
        }

        override fun onResume() {
            if (mSuperPlayer!!.playerState == PlayerState.END) { //重播
                mSuperPlayer!!.reStart()
            } else if (mSuperPlayer!!.playerState == PlayerState.PAUSE) { //继续播放
                mSuperPlayer!!.resume()
            }
        }

        override fun onSeekTo(position: Int) {
            mSuperPlayer!!.seek(position)
        }

        override fun onResumeLive() {
            mSuperPlayer!!.resumeLive()
        }

        override fun onDanmuToggle(isOpen: Boolean) {
            if (mDanmuView != null) {
                mDanmuView!!.toggle(isOpen)
            }
        }

        override fun onSnapshot() {
            mSuperPlayer!!.snapshot { bitmap ->
                if (bitmap != null) {
                    showSnapshotWindow(bitmap)
                } else {
                    showToast(R.string.superplayer_screenshot_fail)
                }
            }
        }

        override fun onQualityChange(quality: VideoQuality) {
            mFullScreenPlayer!!.updateVideoQuality(quality)
            mSuperPlayer!!.switchStream(quality)
        }

        override fun onSpeedChange(speedLevel: Float) {
            mSuperPlayer!!.setRate(speedLevel)
        }

        override fun onMirrorToggle(isMirror: Boolean) {
            mSuperPlayer!!.setMirror(isMirror)
        }

        override fun onHWAccelerationToggle(isAccelerate: Boolean) {
            mSuperPlayer!!.enableHardwareDecode(isAccelerate)
        }
    }

    /**
     * 显示截图窗口
     *
     * @param bmp
     */
    private fun showSnapshotWindow(bmp: Bitmap) {
        val popupWindow = PopupWindow(mContext)
        popupWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.superplayer_layout_new_vod_snap, null)
        val imageView = view.findViewById<View>(R.id.superplayer_iv_snap) as ImageView
        imageView.setImageBitmap(bmp)
        popupWindow.contentView = view
        popupWindow.isOutsideTouchable = true
        popupWindow.showAtLocation(mRootView, Gravity.TOP, 1800, 300)
        AsyncTask.execute { save2MediaStore(mContext, bmp) }
        postDelayed({ popupWindow.dismiss() }, 3000)
    }

    /**
     * 旋转屏幕方向
     *
     * @param orientation
     */
    private fun rotateScreenOrientation(orientation: SuperPlayerDef.Orientation) {
        when (orientation) {
            Orientation.LANDSCAPE -> (mContext as Activity?)!!.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Orientation.PORTRAIT -> (mContext as Activity?)!!.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * 检查悬浮窗权限
     *
     *
     * API <18，默认有悬浮窗权限，不需要处理。无法接收无法接收触摸和按键事件，不需要权限和无法接受触摸事件的源码分析
     * API >= 19 ，可以接收触摸和按键事件
     * API >=23，需要在manifest中申请权限，并在每次需要用到权限的时候检查是否已有该权限，因为用户随时可以取消掉。
     * API >25，TYPE_TOAST 已经被谷歌制裁了，会出现自动消失的情况
     */
    private fun checkOp(context: Context?, op: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val manager = context!!.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                val method = AppOpsManager::class.java.getDeclaredMethod(
                    "checkOp",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
                return AppOpsManager.MODE_ALLOWED == method.invoke(
                    manager,
                    op,
                    Binder.getCallingUid(),
                    context.packageName
                ) as Int
            } catch (e: Exception) {
                TXCLog.e(TAG, Log.getStackTraceString(e))
            }
        }
        return true
    }

    /**
     * SuperPlayerView的回调接口
     */
    interface OnSuperPlayerViewCallback {
        /**
         * 开始全屏播放
         */
        fun onStartFullScreenPlay()

        /**
         * 结束全屏播放
         */
        fun onStopFullScreenPlay()

        /**
         * 点击悬浮窗模式下的x按钮
         */
        fun onClickFloatCloseBtn()

        /**
         * 点击小播放模式的返回按钮
         */
        fun onClickSmallReturnBtn()

        /**
         * 开始悬浮窗播放
         */
        fun onStartFloatWindowPlay()
    }

    fun release() {
        if (mWindowPlayer != null) {
            mWindowPlayer!!.release()
        }
        if (mFullScreenPlayer != null) {
            mFullScreenPlayer!!.release()
        }
        if (mFloatPlayer != null) {
            mFloatPlayer!!.release()
        }
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        try {
            release()
        } catch (e: Throwable) {
            TXCLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    fun switchPlayMode(playerMode: PlayerMode) {
        if (playerMode == PlayerMode.WINDOW) {
            mControllerCallback?.onSwitchPlayMode(PlayerMode.WINDOW)
        } else if (playerMode == PlayerMode.FLOAT) {
            if (mPlayerViewCallback != null) {
                mPlayerViewCallback!!.onStartFloatWindowPlay()
            }
            mControllerCallback?.onSwitchPlayMode(PlayerMode.FLOAT)
        }
    }

    val playerMode: PlayerMode?
        get() = mSuperPlayer!!.playerMode
    val playerState: PlayerState?
        get() = mSuperPlayer!!.playerState
    private val mSuperPlayerObserver: SuperPlayerObserver = object : SuperPlayerObserver() {
        override fun onPlayBegin(name: String?) {
            mWindowPlayer!!.updatePlayState(PlayerState.PLAYING)
            mFullScreenPlayer!!.updatePlayState(PlayerState.PLAYING)
            updateTitle(name)
            mWindowPlayer!!.hideBackground()
            if (mDanmuView != null && mDanmuView!!.isPrepared && mDanmuView!!.isPaused) {
                mDanmuView!!.resume()
            }
            if (mWatcher != null) {
                mWatcher!!.exitLoading()
            }
        }

        override fun onPlayPause() {
            mWindowPlayer!!.updatePlayState(PlayerState.PAUSE)
            mFullScreenPlayer!!.updatePlayState(PlayerState.PAUSE)
        }

        override fun onPlayStop() {
            mWindowPlayer!!.updatePlayState(PlayerState.END)
            mFullScreenPlayer!!.updatePlayState(PlayerState.END)
            // 清空关键帧和视频打点信息
            if (mWatcher != null) {
                mWatcher!!.stop()
            }
            mFullScreenPlayer!!.updateImageSpriteInfo(null)
            mFullScreenPlayer!!.updateKeyFrameDescInfo(null)
        }

        override fun onPlayLoading() {
            mWindowPlayer!!.updatePlayState(PlayerState.LOADING)
            mFullScreenPlayer!!.updatePlayState(PlayerState.LOADING)
            if (mWatcher != null) {
                mWatcher!!.enterLoading()
            }
        }

        override fun onVideoSize(width: Int, height: Int) {
            super.onVideoSize(width, height)

        }

        override fun onPlayProgress(current: Long, duration: Long) {
            mWindowPlayer!!.updateVideoProgress(current, duration)
            mFullScreenPlayer!!.updateVideoProgress(current, duration)
        }

        override fun onSeek(position: Int) {
            if (mSuperPlayer!!.playerType != PlayerType.VOD) {
                if (mWatcher != null) {
                    mWatcher!!.stop()
                }
            }
        }

        override fun onSwitchStreamStart(
            success: Boolean,
            playerType: PlayerType,
            quality: VideoQuality
        ) {
            if (playerType == PlayerType.LIVE) {
                if (success) {
                    Toast.makeText(mContext, "正在切换到" + quality.title + "...", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        mContext,
                        "切换" + quality.title + "清晰度失败，请稍候重试",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        override fun onSwitchStreamEnd(
            success: Boolean,
            playerType: PlayerType,
            quality: VideoQuality?
        ) {
            if (playerType == PlayerType.LIVE) {
                if (success) {
                    Toast.makeText(mContext, "清晰度切换成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "清晰度切换失败", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onPlayerTypeChange(playType: PlayerType?) {
            mWindowPlayer!!.updatePlayType(playType)
            mFullScreenPlayer!!.updatePlayType(playType)
        }

        override fun onPlayTimeShiftLive(player: TXLivePlayer?, url: String?) {
            if (mWatcher == null) {
                mWatcher = NetWatcher(mContext)
            }
            mWatcher!!.start(url, player)
        }

        override fun onVideoQualityListChange(
            videoQualities: List<VideoQuality?>?,
            defaultVideoQuality: VideoQuality?
        ) {
            if (videoQualities != null && !videoQualities.isEmpty()) {
                mFullScreenPlayer!!.setVideoQualityList(videoQualities)
            }
            if (defaultVideoQuality != null) {
                mFullScreenPlayer!!.updateVideoQuality(defaultVideoQuality)
            }
        }

        override fun onVideoImageSpriteAndKeyFrameChanged(
            info: PlayImageSpriteInfo?,
            list: List<PlayKeyFrameDescInfo?>?
        ) {
            mFullScreenPlayer!!.updateImageSpriteInfo(info)
            mFullScreenPlayer!!.updateKeyFrameDescInfo(list)
        }

        override fun onError(code: Int, message: String?) {
            showToast(message)
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(resId: Int) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "SuperPlayerView"
        fun save2MediaStore(context: Context?, image: Bitmap) {
            val sdcardDir = context!!.getExternalFilesDir(null)
            if (sdcardDir == null) {
                Log.e(TAG, "sdcardDir is null")
                return
            }
            val appDir = File(sdcardDir, "superplayer")
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val dateSeconds = System.currentTimeMillis() / 1000
            val fileName = "$dateSeconds.jpg"
            val file = File(appDir, fileName)
            val filePath = file.absolutePath
            val f = File(filePath)
            if (f.exists()) {
                f.delete()
            }
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(f)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            try {
                // Save the screenshot to the MediaStore
                val values = ContentValues()
                val resolver = context.contentResolver
                values.put(MediaStore.Images.ImageColumns.DATA, filePath)
                values.put(MediaStore.Images.ImageColumns.TITLE, fileName)
                values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName)
                values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds)
                values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds)
                values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
                values.put(MediaStore.Images.ImageColumns.WIDTH, image.width)
                values.put(MediaStore.Images.ImageColumns.HEIGHT, image.height)
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val out = resolver.openOutputStream(uri!!)
                image.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out!!.flush()
                out.close()

                // update file size in the database
                values.clear()
                values.put(MediaStore.Images.ImageColumns.SIZE, File(filePath).length())
                resolver.update(uri, values, null, null)
            } catch (e: Exception) {
                TXCLog.e(TAG, Log.getStackTraceString(e))
            }
        }
    }
}