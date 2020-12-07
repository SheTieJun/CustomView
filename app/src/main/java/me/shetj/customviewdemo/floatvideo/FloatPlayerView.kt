package me.shetj.customviewdemo.floatvideo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import me.shetj.base.tools.app.ArmsUtils
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class FloatPlayerView : FrameLayout {
    private var mRect: RectF? = null
    private var videoPlayer: FloatingVideo? = null
    private var mPath: Path? = null
    private val rd = ArmsUtils.dp2px(5f).toFloat()

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    init {
        GSYVideoManager.instance().optionModelList = arrayListOf(
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "crypto,file,http,https,tcp,tls,udp,rtmp,rtsp"),
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1),
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1),
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1),
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_timeout", -1)
        )
    }

    private fun init() {
        mPath = Path()
        mRect = RectF()
        videoPlayer = FloatingVideo(context)
        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        layoutParams.gravity = Gravity.CENTER
        addView(videoPlayer, layoutParams)
        val source1 =
            "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
        videoPlayer!!.setUp(source1, true, "测试视频")
        //是否可以滑动调整
        videoPlayer!!.setIsTouchWiget(false)
        videoPlayer!!.startPlayLogic()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.i("FloatPlayerView", "onAttachedToWindow")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.i("FloatPlayerView", "onDetachedFromWindow")
    }

    fun onPause() {
        videoPlayer!!.currentPlayer.onVideoPause()
    }

    fun onResume() {
        videoPlayer!!.currentPlayer.onVideoResume()
    }

    ////region 剪切圆角
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPath?.reset()
        mRect?.set(0f, 0f, w.toFloat(), h.toFloat())
        mRect?.let {
            mPath?.addRoundRect(mRect!!, rd, rd, Path.Direction.CW)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(mPath!!)
        super.dispatchDraw(canvas)
        canvas.restore()
    }
    //endregion
}