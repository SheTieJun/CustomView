package me.shetj.customviewdemo.pic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.util.concurrent.atomic.AtomicBoolean


class PicBroadcastReceiver : BroadcastReceiver() {
    private val filter = IntentFilter().apply {
        addAction(ACTION_MEDIA_CONTROL)
    }
    private val isRegister: AtomicBoolean = AtomicBoolean(false)
    private var listener: PicCtrlListener? = null

    companion object {
        const val REQUEST_PLAY = 1

        const val REQUEST_PAUSE = 2

        /** Intent action for media controls from Picture-in-Picture mode.
         * 从图片-图片模式中对媒体控制的意图行动。
         */
        const val ACTION_MEDIA_CONTROL = "media_control"

        /** Intent extra for media controls from Picture-in-Picture mode.
         * 从图片图片模式中 为媒体控制提供额外的功能。
         */
        const val EXTRA_CONTROL_TYPE = "control_type"

        /** The intent extra value for play action.  */
        const val CONTROL_TYPE_PLAY = 1

        /** The intent extra value for pause action.  */
        const val CONTROL_TYPE_PAUSE = 2
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null
            || ACTION_MEDIA_CONTROL != intent.action
        ) {
            return
        }
        when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
            CONTROL_TYPE_PLAY -> listener?.play()
            CONTROL_TYPE_PAUSE -> listener?.pause()
        }
    }


    fun registerReceiver(context: Context) {
        if (isRegister.compareAndSet(false,true)) {
            context.registerReceiver(this, filter)
        }
    }

    fun unRegister(context: Context){
        if (isRegister.compareAndSet(true,false)) {
            context.unregisterReceiver(this)
        }
    }
    

    fun setListener(listener: PicCtrlListener) {
        this.listener = listener
    }


    interface PicCtrlListener {

        fun play() {

        }

        fun pause() {

        }

        fun close() {

        }


    }
}