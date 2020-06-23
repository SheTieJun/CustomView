package me.shetj.customviewdemo.floatvideo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.fragment.app.FragmentActivity
import com.shuyu.gsyvideoplayer.GSYVideoManager
import me.shetj.custom.floatview.FloatWindow
import me.shetj.custom.floatview.MoveType
import me.shetj.custom.floatview.Screen
import me.shetj.custom.floatview.Util
import me.shetj.customviewdemo.MainActivity
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.utils.createDialog

fun showDialogFloat(context: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!Util.hasPermission(context)) {
            val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION")
            intent.data = Uri.parse("package:" + context.packageName)
            (context as? FragmentActivity)?.startActivityForResult(intent, 1);
            return
        }
    }
    createDialog(context, R.layout.layout_float_view_setting){
        it.findViewById<View>(R.id.btn_show).setOnClickListener { showFloat(context) }
        it.findViewById<View>(R.id.btn_updateWH).setOnClickListener { updateWH(480,270) }
        it.findViewById<View>(R.id.btn_destroy).setOnClickListener { destroyFloat(true) }
    }?.setOnDismissListener {
        destroyFloat(true)
    }
}


fun showFloat(context: Context){
    if (FloatWindow.get() != null) {
        return
    }
    val floatPlayerView =
        FloatPlayerView(context.applicationContext)
    FloatWindow
        .with(context.applicationContext)
        .setView(floatPlayerView)
        .setWidth(Screen.width, 0.01f)
        .setHeight(Screen.width, 0.01f)
        .setX(Screen.width, 0.8f)
        .setY(Screen.height, 0.8f)
        .setMoveType(MoveType.active)
        .setFilter(true,MainActivity::class.java)
        .setMoveStyle(500, BounceInterpolator())
        .build()
    FloatWindow.get().show()
}

fun updateWH(width:Int,height :Int){
    FloatWindow.get().updateWh(width,height)
}

fun destroyFloat(isStop:Boolean){
    if (isStop){
        GSYVideoManager.instance().releaseMediaPlayer()
    }
    FloatWindow.destroy()
}