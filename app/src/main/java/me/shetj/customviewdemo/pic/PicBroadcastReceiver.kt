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
    private var listener: ArrayList<PicCtrlListener>  = ArrayList()

    companion object {
        const val REQUEST_PLAY = 1

        const val REQUEST_PAUSE = 2

        /** The request code for info action PendingIntent.  */
        const val REQUEST_INFO = 3

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

        /** The intent extra value for pause action.  */
        const val CONTROL_TYPE_ClOSE = 3
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null
            || ACTION_MEDIA_CONTROL != intent.action
        ) {
            return
        }
        when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
            CONTROL_TYPE_PLAY -> {
                listener.forEach {
                    it.play()
                }
            }
            CONTROL_TYPE_PAUSE -> {
                listener.forEach {
                    it.pause()
                }
            }
            CONTROL_TYPE_ClOSE ->{
                listener.forEach {
                    it.close()
                }
            }
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
    

    fun addListener(listener: PicCtrlListener) {
        if (!this.listener.contains(listener)) {
            this.listener.add(listener)
        }
    }

    fun removeListener(listener: PicCtrlListener){
        this.listener.remove(listener)
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