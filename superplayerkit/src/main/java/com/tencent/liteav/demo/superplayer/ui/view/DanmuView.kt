package com.tencent.liteav.demo.superplayer.ui.view

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import com.tencent.liteav.basic.log.TXCLog
import com.tencent.liteav.demo.superplayer.ui.view.DanmuView.Companion.MSG_SEND_DANMU
import com.tencent.liteav.demo.superplayer.ui.view.DanmuView.DanmuHandler
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.ui.widget.DanmakuView
import java.util.*

/**
 * Created by liyuejiao on 2018/1/29.
 *
 * 全功能播放器中的弹幕View
 *
 * 1、随机发送弹幕[.addDanmaku]
 *
 * 2、弹幕操作所在线程的Handler[DanmuHandler]
 */
class DanmuView : DanmakuView {
    private var mContext: Context? = null
    private var mDanmakuContext: DanmakuContext? = null
    private var mShowDanma // 弹幕是否开启
            = false
    private var mHandlerThread // 发送弹幕的线程
            : HandlerThread? = null
    private var mDanmuHandler // 弹幕线程handler
            : DanmuHandler? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        enableDanmakuDrawingCache(true)
        setCallback(object : DrawHandler.Callback {
            override fun prepared() {
                mShowDanma = true
                start()
                generateDanmaku()
            }

            override fun updateTimer(timer: DanmakuTimer) {}
            override fun danmakuShown(danmaku: BaseDanmaku) {}
            override fun drawingFinished() {}
        })
        mDanmakuContext = DanmakuContext.create()
        prepare(mParser, mDanmakuContext)
    }

    override fun release() {
        super.release()
        mShowDanma = false
        if (mDanmuHandler != null) {
            mDanmuHandler!!.removeCallbacksAndMessages(null)
            mDanmuHandler = null
        }
        if (mHandlerThread != null) {
            mHandlerThread!!.quit()
            mHandlerThread = null
        }
    }

    private val mParser: BaseDanmakuParser = object : BaseDanmakuParser() {
        override fun parse(): IDanmakus {
            return Danmakus()
        }
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private fun generateDanmaku() {
        mHandlerThread = HandlerThread("Danmu")
        mHandlerThread!!.start()
        mDanmuHandler = DanmuHandler(mHandlerThread!!.looper)
    }

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private fun addDanmaku(content: String, withBorder: Boolean) {
        val danmaku = mDanmakuContext!!.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL)
        if (danmaku != null) {
            danmaku.text = content
            danmaku.padding = 5
            danmaku.textSize = sp2px(mContext, 20.0f).toFloat()
            danmaku.textColor = Color.WHITE
            danmaku.time = currentTime
            if (withBorder) {
                danmaku.borderColor = Color.GREEN
            }
            addDanmaku(danmaku)
        }
    }

    /**
     * sp单位转px
     *
     * @param context
     * @param spValue
     * @return
     */
    fun sp2px(context: Context?, spValue: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (spValue * scale + 0.5f).toInt()
    }

    fun toggle(on: Boolean) {
        TXCLog.i(TAG, "onToggleControllerView on:$on")
        if (on) {
            mDanmuHandler!!.sendEmptyMessageAtTime(MSG_SEND_DANMU, 100)
        } else {
            mDanmuHandler!!.removeMessages(MSG_SEND_DANMU)
        }
    }

    inner class DanmuHandler(looper: Looper?) : Handler(looper!!) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Companion.MSG_SEND_DANMU -> {
                    sendDanmu()
                    val time = Random().nextInt(1000)
                    mDanmuHandler!!.sendEmptyMessageDelayed(Companion.MSG_SEND_DANMU, time.toLong())
                }
            }
        }

        private fun sendDanmu() {
            val time = Random().nextInt(300)
            val content = "弹幕$time$time"
            addDanmaku(content, false)
        }

    }

    companion object {
        const val MSG_SEND_DANMU = 1001
    }
}